--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/default/kernel/insert-permissions.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $


insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -204, 'admin', currentDate());
insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -300, 'admin', currentDate());
