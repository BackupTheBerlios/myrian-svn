-- Create a permission for the ACS system party.

insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -204, 'admin', sysdate);
insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -300, 'admin', sysdate);
