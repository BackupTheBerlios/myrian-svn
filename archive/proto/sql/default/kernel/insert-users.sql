-----------------------
-- Virtual Users     --
-----------------------

insert into acs_objects (object_id, object_type, display_name) 
values (-200, 'com.arsdigita.kernel.User', 'The Public');
insert into parties (party_id, primary_email) values (-200, 'public@nullhost');
insert into person_names (name_id, given_name, family_name) values
(-201, 'Public', 'Users');
insert into users (user_id, name_id) values (-200, -201);
insert into email_addresses values ('public@nullhost', '1', '0');
insert into acs_objects (object_id, object_type, display_name) 
values (-202, 'com.arsdigita.kernel.User', 'Registered Users');
insert into parties (party_id, primary_email) values (-202, 'registered@nullhost');
insert into person_names (name_id, given_name, family_name) values
(-203, 'Registered', 'Users');
insert into users (user_id, name_id) values (-202, -203);
insert into email_addresses values ('registered@nullhost', '1', '0');
insert into acs_objects (object_id, object_type, display_name) 
values (-204, 'com.arsdigita.kernel.Party', 'ACS System Party');
insert into parties (party_id, primary_email) 
values (-204, 'acs-system-party@acs-system');
