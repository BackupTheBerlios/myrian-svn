@@ permissions-remove-context-child-counts.sql

insert into acs_privileges
(privilege)
values
('edit');


insert into acs_objects (object_id, object_type, display_name) 
values (-204, 'com.arsdigita.kernel.Party', 'ACS System Party');
insert into parties (party_id, primary_email) 
values (-204, 'acs-system-party@acs-system');

-- Create a permission for the ACS system party.

insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -204, 'admin', sysdate);
