--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/permissions-4.6.3-4.6.4.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


create index acs_perm_grantee_priv_idx
    on acs_permissions (grantee_id, privilege);

create unique index gtci_context_obj_idx
    on granted_trans_context_index (implied_context_id, object_id);

---- For some reason, this index results in bad oracle errors for
---- the triggers on insert into / delete from object_context.
---- It doesn't seem to impact performance enough to try to make
---- the triggers work with this index, so for now, we'll just
---- leave out the index.
--
-- create unique index utci_context_obj_idx
--      on ungranted_trans_context_index (implied_context_id, object_id);


--
create or replace package body permission_denormalization
as

  procedure add_context (
    object_id   in object_context.object_id%TYPE,
    context_id  in object_context.context_id%TYPE
  )
  as
  begin
    insert into granted_trans_context_index
    (object_id, implied_context_id, n_generations)
    select descendants.object_id, ancestors.implied_context_id, 
           (descendants.n_generations + 
               ancestors.n_generations + 1) as n_generations
    from granted_trans_context_index ancestors,
         (select object_id, implied_context_id, n_generations
          from object_context_trans_map
          UNION
          select add_context.object_id, add_context.object_id, 0
          from dual) descendants
    where ancestors.object_id = add_context.context_id
      and descendants.implied_context_id = add_context.object_id;
    
    INSERT into ungranted_trans_context_index
    (object_id, implied_context_id, n_generations)
    select descendants.object_id, ancestors.implied_context_id, 
           (descendants.n_generations + 
               ancestors.n_generations + 1) as n_generations
    from (select object_id, implied_context_id, n_generations
          from ungranted_trans_context_index
          UNION ALL
          select add_context.context_id, add_context.context_id, 0
          from dual
          where not exists (select 1 from object_grants
                            where object_id=add_context.context_id
                            and n_grants>0)) ancestors,
         (select object_id, implied_context_id, n_generations
          from object_context_trans_map
          UNION
          select add_context.object_id, add_context.object_id, 0
          from dual) descendants
    where ancestors.object_id = add_context.context_id
      and descendants.implied_context_id = add_context.object_id;

  end add_context;

  procedure remove_context (
    object_id   in object_context.object_id%TYPE,
    context_id  in object_context.context_id%TYPE
  )
  as
  begin

    delete from granted_trans_context_index
    where (   object_id in (select object_id
                            from object_context_trans_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from object_context_trans_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));

    delete from ungranted_trans_context_index
    where (   object_id in (select object_id
                            from object_context_trans_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from object_context_trans_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));

  end remove_context;

  procedure add_grant (
    object_id   in acs_objects.object_id%TYPE
  )
  as
  begin

    update object_grants
    set n_grants = n_grants +1
    where object_id = add_grant.object_id;

    if SQL%NOTFOUND then
        insert into object_grants
        (object_id, n_grants)
        values
        (add_grant.object_id, 1);

        -- insert a row stating that this object has itself as an implied 
        -- context
        insert into granted_trans_context_index
        (object_id, implied_context_id, n_generations)
        values
        (add_grant.object_id, add_grant.object_id, 0);

        -- insert rows in granted_trans_context_index for this object's
        -- "children" -- i.e., all objects that have add_grant.object_id as
        -- their context.
        insert into granted_trans_context_index
        (object_id, implied_context_id, n_generations)
        select object_id, implied_context_id, n_generations
        from ungranted_trans_context_index utci
        where utci.implied_context_id = add_grant.object_id;

        -- remove the same rows from ungranted_trans_context_index
        delete from ungranted_trans_context_index
        where implied_context_id = add_grant.object_id;

    end if;

  end add_grant;

  procedure remove_grant (
    object_id   in acs_objects.object_id%TYPE
  )
  as
    v_n_grants integer;
  begin

    select n_grants into v_n_grants
    from object_grants
    where object_id = remove_grant.object_id;
    -- if the above select fails to return rows, then something is hosed 
    -- because remove_grant should only run when a grant is being removed 
    -- from acs_permisisons in which case add_grant must have been run
    -- when that grant was originally inserted into acs_permissions.

    if (v_n_grants=1) then
        -- remove the row from object_grants because this object has
        -- no grants left.
        delete from object_grants where object_id = remove_grant.object_id;

        -- insert rows in ungranted_trans_context_index for this object's
        -- "children" -- i.e., all objects that have remove_grant.object_id as
        -- their context.
        -- NOTE: we leave out the mapping between an object and itself,
        -- primarily because it makes the implementation of add_grant()
        -- easier.
        insert into ungranted_trans_context_map
        (object_id, implied_context_id, n_generations)
        select object_id, implied_context_id, n_generations
        from granted_trans_context_map gtcm
        where gtcm.implied_context_id = remove_grant.object_id
          and object_id!=implied_context_id;

        -- remove the same rows from the granted_trans_context_index
        delete from granted_trans_context_map
        where implied_context_id = remove_grant.object_id;

    else
        -- decrement the count of grants for this object
        update object_grants
        set n_grants = n_grants - 1
        where object_id = remove_grant.object_id;

    end if;

  end remove_grant;

end;
/
show errors


