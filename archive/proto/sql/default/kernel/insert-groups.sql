-----------------------
-- Default Groups    --
-----------------------

insert into acs_objects (object_id, object_type, display_name) 
values (-300, 'com.arsdigita.kernel.Group', 'Site-wide Administrators');
insert into parties (party_id, primary_email) values (-300, 'admins@nullhost');
insert into groups(group_id, name)
values(-300, 'Site-wide Administrators');
