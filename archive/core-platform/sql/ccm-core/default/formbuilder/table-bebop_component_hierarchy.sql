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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/table-bebop_component_hierarchy.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table bebop_component_hierarchy (
       container_id              integer
                                 constraint bebop_component_hierarchyci_fk
                                 references bebop_components (component_id),
       component_id              integer
                                 constraint bebop_component_hierarchyco_fk
                                 references bebop_components(component_id),
       order_number              integer,
       selected_p                char(1),
       constraint bebop_component_hierarchy_un
       unique(container_id, component_id)
);
