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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/insert-acs_privilege_hierarchy.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('create', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('delete', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('edit', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('map_to_category', 'admin');

insert into acs_privilege_hierarchy (privilege, child_privilege)
  select 'admin', privilege 
    from acs_privileges 
    where privilege not in (select 'admin' from dual union all select child_privilege 
                              from acs_privilege_hierarchy 
                              where privilege = 'admin');
