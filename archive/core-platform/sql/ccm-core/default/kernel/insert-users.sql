--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/insert-users.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $


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
