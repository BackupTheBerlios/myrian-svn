--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/index-acs_permissions.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create index acs_perm_grantee_priv_idx
    on acs_permissions (grantee_id, privilege);
create index acs_perm_creation_user_idx on acs_permissions(creation_user);
create index acs_perm_object_id_idx on acs_permissions(object_id);
