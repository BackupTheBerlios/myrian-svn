--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $
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
