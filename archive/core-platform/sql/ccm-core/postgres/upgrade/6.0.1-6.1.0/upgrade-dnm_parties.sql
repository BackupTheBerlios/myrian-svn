create or replace function upgrade_dnm_parties ()
  returns integer as '
  declare 
    c record;
  begin
   for c in select grantee_id from acs_permissions loop
     perform dnm_parties_add_grant(c.grantee_id);
   end loop;

   for c in select group_id, subgroup_id  from group_subgroup_map loop 
     perform dnm_parties_add_group_subgroup_map(c.group_id, c.subgroup_id);
   end loop;

   for c in select group_id, member_id from group_member_map loop
     perform dnm_parties_add_group_user_map (c.group_id, c.member_id);
   end loop;
   
   return null;
end; ' language 'plpgsql'
;

select upgrade_dnm_parties();

drop function upgrade_dnm_parties();
