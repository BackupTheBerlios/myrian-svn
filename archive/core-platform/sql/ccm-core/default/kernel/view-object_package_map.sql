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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/view-object_package_map.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create view object_package_map as
select o.object_id, p.package_id
from acs_objects o, apm_packages p
where p.package_id=o.object_id 
   or p.package_id in (select container_id
                       from object_container_map
                       start with object_id = o.object_id
                       connect by prior container_id = object_id);
