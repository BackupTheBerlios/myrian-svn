--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/default/kernel/index-acs_permissions.sql#3 $
-- $DateTime: 2003/08/15 13:46:34 $

create index acs_perm_grantee_priv_idx
    on acs_permissions (grantee_id, privilege);
create index acs_perm_creation_user_idx on acs_permissions(creation_user);
create index acs_perm_object_id_idx on acs_permissions(object_id);
