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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/insert-groups.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $


insert into acs_objects (object_id, object_type, display_name) 
values (-300, 'com.arsdigita.kernel.Group', 'Site-wide Administrators');
insert into parties (party_id, primary_email) values (-300, 'admins@nullhost');
insert into groups(group_id, name)
values(-300, 'Site-wide Administrators');
