begin
 for c in (select grantee_id from acs_permissions) loop
   dnm_parties.add_grant(c.grantee_id);
 end loop;

 for c in (select group_id, subgroup_id  from group_subgroup_map) loop 
   dnm_parties.add_group_subgroup_map(c.group_id, c.subgroup_id);
 end loop;

 for c in (select group_id, member_id from group_member_map) loop
   dnm_parties.add_group_user_map (c.group_id, c.member_id);
 end loop;

end;
/
