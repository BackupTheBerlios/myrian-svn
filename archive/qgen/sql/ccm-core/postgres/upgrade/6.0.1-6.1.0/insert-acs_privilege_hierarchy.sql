insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('create', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('delete', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('edit', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('map_to_category', 'admin');

insert into acs_privilege_hierarchy (privilege, child_privilege)
  select 'admin', privilege 
    from acs_privileges 
    where privilege not in (select 'admin' from dual union all select child_privilege 
                              from acs_privilege_hierarchy 
                              where privilege = 'admin');
