begin
  for c in (select privilege from acs_privileges) loop
    dnm_priv_utils.add_privilege(c.privilege);
  end loop;
  for c in (select privilege, child_privilege from acs_privilege_hierarchy) loop
    dnm_priv_utils.add_child_privilege(c.privilege, c.child_privilege);
  end loop;
  for c in (select privilege, grantee_id, object_id from acs_permissions) loop
    dnm_priv_utils.add_grant(c.object_id, c.grantee_id, c.privilege);
  end loop;
end;
/
