create or replace function upgrade_dnm_privileges () 
  returns integer as '
  declare
    c record;
  begin
    for c in select privilege from acs_privileges loop
      perform dnm_privileges_add_privilege(c.privilege);
    end loop;
    for c in select privilege, child_privilege from acs_privilege_hierarchy loop
      perform dnm_privileges_map_add_child_priv(c.privilege, c.child_privilege);
    end loop;
    for c in select privilege, grantee_id, object_id from acs_permissions loop
      perform dnm_privileges_add_grant(c.object_id, c.grantee_id, c.privilege);
    end loop;
    return null;
  end; ' language 'plpgsql'
;

select upgrade_dnm_privileges();

drop function upgrade_dnm_privileges();
