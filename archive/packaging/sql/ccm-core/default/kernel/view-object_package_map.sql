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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/kernel/view-object_package_map.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

create view object_package_map as
select o.object_id, p.package_id
from acs_objects o, apm_packages p
where p.package_id=o.object_id 
   or p.package_id in (select container_id
                       from object_container_map
                       start with object_id = o.object_id
                       connect by prior container_id = object_id);
