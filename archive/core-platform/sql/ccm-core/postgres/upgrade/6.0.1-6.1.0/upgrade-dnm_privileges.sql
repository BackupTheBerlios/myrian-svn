--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $
create or replace function upgrade_dnm_privileges () 
  returns integer as '
  declare
    c record;
  begin
    for c in select privilege from acs_privileges loop
      perform dnm_privileges_add_privilege(c.privilege);
    end loop;
    for c in select privilege, child_privilege from acs_privilege_hierarchy loop
      perform dnm_privileges_map_add_child_priv(c.privilege, c.child_privilege);
    end loop;
    for c in select privilege, grantee_id, object_id from acs_permissions loop
      perform dnm_privileges_add_grant(c.object_id, c.grantee_id, c.privilege);
    end loop;
    return null;
  end; ' language 'plpgsql'
;

select upgrade_dnm_privileges();

drop function upgrade_dnm_privileges();
