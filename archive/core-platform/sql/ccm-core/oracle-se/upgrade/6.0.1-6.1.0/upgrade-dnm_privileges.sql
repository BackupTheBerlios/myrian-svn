--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $
begin
  for c in (select privilege from acs_privileges) loop
    dnm_priv_utils.add_privilege(c.privilege);
  end loop;
  for c in (select privilege, child_privilege from acs_privilege_hierarchy) loop
    dnm_priv_utils.add_child_privilege(c.privilege, c.child_privilege);
  end loop;
  for c in (select privilege, grantee_id, object_id from acs_permissions) loop
    dnm_priv_utils.add_grant(c.object_id, c.grantee_id, c.privilege);
  end loop;
end;
/
