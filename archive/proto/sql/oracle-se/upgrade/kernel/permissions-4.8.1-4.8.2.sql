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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/kernel/permissions-4.8.1-4.8.2.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $



---------------------------------
-- DROP OLD TRIGGERS AND PLSQL --
---------------------------------

drop trigger object_context_in_tr;
drop trigger object_context_up_tr;
drop trigger object_context_del_tr;

drop package permission_denormalization;

-----------------------------
-- CONTEXT DENORMALIZATION --
-----------------------------

-- context hierarchy is denormalized into 2 tables.
-- The union of these 2 tables contains mapping from objects to their
-- implied contexts.  Every object has itself as an implied context, BUT
-- this implicit mapping is only entered into these denormalizations when
-- the object in question has a permission granted on it.
-- The structure of these denormalizaitons is primarily geared towards
-- optimization of permissions checks.  A secondary objective is to
-- minimize the cost of inserting objects, setting their context, and
-- granting permissions on them.  Finally, these denormalizations may
-- prove useful for permissions UI, e.g., "display all objects that
-- inherit permissions from X".

-- This table holds the mappings between object and implied contexts
-- where the implied contexts have direct grant(s).
create table granted_context_non_leaf_map (
       object_id            integer not null
                            constraint gcnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   constraint gcnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint gcnlm_generation_ck
                                check (n_generations >= 0),
       constraint gcnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
) organization index;

-- This table holds the mappings between object and implied contexts
-- where the implied contexts have *no* direct grant(s).
create table ungranted_context_non_leaf_map (
       object_id            integer not null
                            constraint ucnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   constraint ucnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint ucnlm_generation_ck
                                check (n_generations >= 0),
       constraint ucnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
) organization index;

-- this table holds a count of "children" for each object, and is only
-- used in order to maintain the *trans_context_index denormalizations
create table context_child_counts (
   object_id  integer not null
              constraint ccc_object_id_fk
              references acs_objects (object_id)
              constraint ccc_object_id_pk
              primary key,
   n_children integer default 1 not null
              constraint ccc_n_children_ck
              check (n_children>=1)
) organization index;

-- need another copy of object-context mappings in order to avoid mutation 
-- errors in the triggers
create table object_context_map (
       object_id            integer not null
                            constraint ocm_object_id_fk 
                            references acs_objects (object_id)
                            constraint ocm_object_id_pk primary key,
       context_id           constraint ocm_context_id_fk
                            references acs_objects(object_id)
) organization index;

-- foreign key index
create index ocm_context_id_idx on object_context_map(context_id);



--------------------
-- DATA MIGRATION --
--------------------

insert into context_child_counts (object_id, n_children)
select context_id, count(*) from object_context
group by context_id
having count(*) > 0;

insert into object_context_map (object_id, context_id)
select object_id, context_id from object_context;

insert into object_context (object_id, context_id)
select o.object_id, null
from acs_objects o, object_context c
where o.object_id = c.object_id(+)
  and c.object_id is null;

insert into granted_context_non_leaf_map
select map.object_id, map.implied_context_id, map.n_generations
from granted_trans_context_map map, 
     context_child_counts c
where map.object_id = c.object_id;

insert into ungranted_context_non_leaf_map
select map.object_id, map.implied_context_id, map.n_generations
from ungranted_trans_context_map map,
     context_child_counts c
where map.object_id = c.object_id;

drop table granted_trans_context_index;
drop table ungranted_trans_context_index;


----------------------------
-- VIEWS, TRIGGERS, PLSQL --
----------------------------

--
-- Views on the above denormalizations
--
create or replace view all_context_non_leaf_map
as select object_id, implied_context_id, n_generations
   from granted_context_non_leaf_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_context_non_leaf_map;


--
-- pl/sql procedures to help maintain the denormalizations
--
create or replace package permission_denormalization
as
  procedure add_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  );
  procedure remove_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  );
  procedure add_grant (
    object_id   in acs_objects.object_id%TYPE
  );
  procedure remove_grant (
    object_id   in acs_objects.object_id%TYPE
  );
end;
/
show errors

create or replace package body permission_denormalization
as

  procedure add_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  )
  as
  begin

    insert into object_context_map
    (object_id, context_id)
    values
    (add_context.object_id, add_context.context_id);

    update context_child_counts
    set n_children = n_children + 1
    where object_id = add_context.context_id;

    if SQL%NOTFOUND then
        insert into context_child_counts
        (object_id, n_children)
        values
        (add_context.context_id, 1);

            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select add_context.context_id, ancestors.implied_context_id, 
                   (ancestors.n_generations + 1) as n_generations
            from object_context_map, granted_context_non_leaf_map ancestors
            where object_context_map.object_id = add_context.context_id
              and ancestors.object_id = object_context_map.context_id
            UNION ALL
            select add_context.context_id, add_context.context_id, 0
            from object_grants
            where object_id = add_context.context_id;

            insert into ungranted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select add_context.context_id, ancestors.implied_context_id, 
                   (ancestors.n_generations + 1) as n_generations
            from object_context_map, 
                 (select object_id, implied_context_id, n_generations
                  from ungranted_context_non_leaf_map
                  UNION ALL
                  select context_id, context_id, 0
                  from object_context_map c
                  where c.object_id = add_context.context_id
                    and not exists (select 1 from object_grants
                                    where object_id=c.context_id
                                    and n_grants>0)) ancestors
            where object_context_map.object_id = add_context.context_id
              and ancestors.object_id = object_context_map.context_id;

    end if;

      insert into granted_context_non_leaf_map
      (object_id, implied_context_id, n_generations)
      select descendants.object_id, ancestors.implied_context_id, 
             (descendants.n_generations + 
                 ancestors.n_generations + 1) as n_generations
      from granted_context_non_leaf_map ancestors,
           (select object_id, implied_context_id, n_generations
            from all_context_non_leaf_map
            UNION
            select add_context.object_id, add_context.object_id, 0
            from context_child_counts
            where object_id = add_context.object_id) descendants
      where ancestors.object_id = add_context.context_id
        and descendants.implied_context_id = add_context.object_id;
    
      INSERT into ungranted_context_non_leaf_map
      (object_id, implied_context_id, n_generations)
      select descendants.object_id, ancestors.implied_context_id, 
             (descendants.n_generations + 
                 ancestors.n_generations + 1) as n_generations
      from (select object_id, implied_context_id, n_generations
            from ungranted_context_non_leaf_map
            UNION ALL
            select add_context.context_id, add_context.context_id, 0
            from dual
            where not exists (select 1 from object_grants
                              where object_id=add_context.context_id
                              and n_grants>0)) ancestors,
           (select object_id, implied_context_id, n_generations
            from all_context_non_leaf_map
            UNION
            select add_context.object_id, add_context.object_id, 0
            from context_child_counts
            where object_id = add_context.object_id) descendants
      where ancestors.object_id = add_context.context_id
        and descendants.implied_context_id = add_context.object_id;

  end add_context;

  procedure remove_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  )
  as
    v_delete_context integer;
  begin

    delete from object_context_map 
    where object_id = remove_context.object_id;

    delete from context_child_counts
    where object_id = remove_context.context_id
      and n_children=1;

    if SQL%NOTFOUND then
        update context_child_counts
        set n_children = n_children - 1
        where object_id = remove_context.context_id;

        v_delete_context := 0;
    else
        v_delete_context := 1;
    end if;

    delete from granted_context_non_leaf_map
    where (   object_id in (select object_id
                            from all_context_non_leaf_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from all_context_non_leaf_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));

    delete from ungranted_context_non_leaf_map
    where (   object_id in (select object_id
                            from all_context_non_leaf_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from all_context_non_leaf_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));


    if (v_delete_context = 1) then
        -- the context has no more "children", so remove it
        -- from the denormalization.
        delete from granted_context_non_leaf_map
        where object_id = remove_context.context_id;

        delete from ungranted_context_non_leaf_map
        where object_id = remove_context.context_id;
    end if;

  end remove_context;

  procedure add_grant (
    object_id   in acs_objects.object_id%TYPE
  )
  as
      v_has_children integer;
  begin

    update object_grants
    set n_grants = n_grants +1
    where object_id = add_grant.object_id;

    if SQL%NOTFOUND then
        insert into object_grants
        (object_id, n_grants)
        values
        (add_grant.object_id, 1);

        select count(*) into v_has_children
        from context_child_counts
        where object_id = add_grant.object_id;

        if (v_has_children=1) then

            -- insert a row stating that this object has itself as an 
            -- implied context
            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            values
            (add_grant.object_id, add_grant.object_id, 0);

            -- insert rows in granted_context_non_leaf_map for this object's
            -- "children" -- i.e., all objects that have add_grant.object_id as
            -- their context.
            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select object_id, implied_context_id, n_generations
            from ungranted_context_non_leaf_map utci
            where utci.implied_context_id = add_grant.object_id;

            -- remove the same rows from ungranted_context_non_leaf_map
            delete from ungranted_context_non_leaf_map
            where implied_context_id = add_grant.object_id;

        end if;
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

            -- insert rows in ungranted_context_non_leaf_map for this object's
            -- "children" -- i.e., all objects that have 
            -- remove_grant.object_id as their context.
            -- NOTE: we leave out the mapping between an object and itself,
            -- primarily because it makes the implementation of add_grant()
            -- easier.
            insert into ungranted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select object_id, implied_context_id, n_generations
            from granted_context_non_leaf_map gtcm
            where gtcm.implied_context_id = remove_grant.object_id
              and object_id!=implied_context_id;

            -- remove the same rows from the granted_context_non_leaf_map
            delete from granted_context_non_leaf_map
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


create or replace trigger object_context_in_tr
after insert on object_context
for each row
begin
  if :new.context_id is not null then
    permission_denormalization.add_context(:new.object_id, :new.context_id);
  end if;
end;
/
show errors

create or replace trigger object_context_up_tr
after update on object_context
for each row
begin
  if ((:old.context_id != :new.context_id) or
      (:old.context_id is null) or
      (:new.context_id is null)) then

    if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
    end if;
    if :new.context_id is not null then
      permission_denormalization.add_context(:new.object_id, :new.context_id);
    end if;

  end if;
end;
/
show errors

create or replace trigger object_context_del_tr
before delete on object_context
for each row
begin
  if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
  end if;
end;
/
show errors

create or replace trigger acs_objects_context_in_tr
after insert on acs_objects
for each row
begin
   insert into object_context
   (object_id, context_id)
   values
   (:new.object_id, null);
end;
/
show errors

--
-- Triggers on acs_permissions to maintain above denormalizations
--

create or replace trigger acs_permissions_in_tr
after insert on acs_permissions
for each row
begin
    permission_denormalization.add_grant(:new.object_id);
end;
/
show errors

-- this trigger supports a fringe case where someone updates a
-- a grant (i.e. row in acs_permissions) and chagnes the object_id.
create or replace trigger acs_permissions_up_tr
after update on acs_permissions
for each row
begin
    if (:old.object_id != :new.object_id) then
        permission_denormalization.remove_grant(:old.object_id);
        permission_denormalization.add_grant(:new.object_id);
    end if;
end;
/
show errors

create or replace trigger acs_permissions_del_tr
after delete on acs_permissions
for each row
begin
    permission_denormalization.remove_grant(:old.object_id);
end;
/
show errors

-------------
-- INDEXES --
-------------

create unique index gcnlm_context_obj_idx
    on granted_context_non_leaf_map (implied_context_id, object_id);


-----------------------
-- DEPRECTATED VIEWS --
-----------------------

-- These views are here to prevent old code from breaking.  These
-- views do not necessarily perform acceptably.

create or replace view granted_trans_context_index
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, granted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select object_id, object_id, 0
from object_grants;

create or replace view granted_trans_context_map
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, granted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select object_id, object_id, 0
from object_grants;

create or replace view ungranted_trans_context_index
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, ungranted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select o.object_id, o.context_id, 1
from object_context_map o, object_grants g
where o.object_id = g.object_id(+) and g.object_id=null;

create or replace view ungranted_trans_context_map
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, ungranted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select o.object_id, o.context_id, 1
from object_context_map o, object_grants g
where o.object_id = g.object_id(+) and g.object_id=null;

-- Use with caution.  For some objects, this view will contain
-- the implicit mapping (object_id, object_id, 0).  For some objects this
-- view WILL NOT contain this implicit mapping.
create or replace view object_context_trans_map
as select object_id, implied_context_id, n_generations
   from granted_trans_context_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_trans_context_map;
