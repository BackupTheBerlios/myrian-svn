--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/kernel/package-dnm_parties.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create or replace function dnm_parties_add_group_subgroup_map (
  integer, integer )
  returns integer as '
  declare
    p_group_id alias for $1;
    p_subgroup_id alias for $2;
  begin
    -- insert new parent groups for p_subgroup_id
    insert into dnm_group_membership 
      (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
      select pd_group_id, p_subgroup_id, 0, 0
        from dnm_group_membership dn
	where pd_member_id = p_group_id
          and not exists (select 1 from dnm_group_membership dn1
		            where dn1.pd_group_id = dn.pd_group_id
			      and dn1.pd_member_id = p_subgroup_id)
    ;

    -- insert new parent groups for existing subgroups of p_subgroup_id
    insert into dnm_group_membership
      (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
      select dn.pd_group_id, gsti.subgroup_id , 0 , 
          case when dn.pd_group_id-gsti.subgroup_id = 0 then 1
               else 0
          end
	from dnm_group_membership dn, group_subgroup_trans_index gsti
	where dn.pd_member_id = p_group_id
	  and gsti.group_id = p_subgroup_id
          and not exists (select 1 from dnm_group_membership dn1
		            where dn1.pd_group_id = dn.pd_group_id
			      and dn1.pd_member_id = gsti.subgroup_id)

    ;

    -- insert new parent groups for member of subgroup and its subgroups
    insert into dnm_group_membership
      (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
      select dn.pd_group_id, gmti.member_id, 1, 0
        from dnm_group_membership dn, group_member_trans_index gmti
	where dn.pd_member_id = p_group_id
	  and gmti.group_id = p_subgroup_id
          and not exists (select 1 from dnm_group_membership dn1
		            where dn1.pd_group_id = dn.pd_group_id
			      and dn1.pd_member_id = gmti.member_id)
    ;

    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_parties_delete_map (
  integer, integer )
  returns integer as '
  declare 
    p_group_id alias for $1;
    p_subgroup_id alias for $2;
  begin
    delete from dnm_group_membership 
      where pd_group_id = p_group_id and pd_member_id = p_subgroup_id;
    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_parties_add_group_user_map (
  integer, integer )
  returns integer as '
  declare 
    p_group_id alias for $1;
    p_user_id alias for $2;
  begin
    -- insert tuple(implied group_id, user_id)
    insert into dnm_group_membership 
      (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
      select dgm.pd_group_id, p_user_id, 1, 0
        from dnm_group_membership dgm 
        where dgm.pd_member_id = p_group_id
	  and not exists (select 1 from dnm_group_membership dg 
		            where dg.pd_group_id = dgm.pd_group_id 
			      and dg.pd_member_id = p_user_id);
    return null;
  end; ' language 'plpgsql'
;


create or replace function dnm_parties_add_grant (
  integer )
  returns integer as '
  declare
    p_party_id alias for $1;
    v_grants integer;
    v_is_user integer;
  begin

    -- get the party_id and lock the appropriate row in dnm_party_grants 
    -- if it exists. Locking neccesary here to avoid problmes with concurrent
    -- actions on dnm_party_grants
      select pd_n_grants into v_grants
        from dnm_party_grants
        where pd_party_id = p_party_id
        for update
      ;

    if (v_grants > 0) then

      update dnm_party_grants set pd_n_grants = pd_n_grants +1 
        where pd_party_id = p_party_id
      ;

    else 

      insert into dnm_party_grants (pd_party_id, pd_n_grants) values (p_party_id, 1);

      select count(user_id) into v_is_user 
        from users where user_id = p_party_id;

      if v_is_user > 0 then
        -- if p_party_id is user then add pd_self_map into dnm_group_membership
        insert into dnm_group_membership 
          (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
          values(p_party_id, p_party_id, 1, 1)
        ;
      else
        -- p_party_id is a group, add group subgroup rows into 
        --   dnm_group_membership for p_party_id
        insert into dnm_group_membership 
          (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
          select group_id, subgroup_id, 0, 
              case when group_id - subgroup_id = 0 then 1
                else 0
              end
            from group_subgroup_trans_index
            where group_id = p_party_id
        ;
        -- add user group mapping to dnm_group_membership for p_party_id
        insert into dnm_group_membership 
          (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map)
          select group_id, member_id, 1,  0 
            from group_member_trans_index
            where group_id = p_party_id
        ;
      end if;

    end if;

    return null;

  end; ' language 'plpgsql'
;


create or replace function dnm_parties_remove_grant (
  integer )
  returns integer as '
  declare
    p_party_id alias for $1;
    v_grants integer;
  begin 
    select pd_n_grants into v_grants
      from dnm_party_grants
      where pd_party_id = p_party_id
      for update 
    ;
    if v_grants > 1 then
       update dnm_party_grants set pd_n_grants = pd_n_grants -1 
         where pd_party_id = p_party_id;
    else 
       -- now no direct grants are associtated with p_party_id
       -- delete all appropriate rows from dnm_party_grants and
       -- dnm_group_membership
       delete from dnm_group_membership where pd_group_id = p_party_id;
       delete from dnm_party_grants where pd_party_id = p_party_id;
    end if;
    
    return null;
  end; ' language 'plpgsql'
;    
