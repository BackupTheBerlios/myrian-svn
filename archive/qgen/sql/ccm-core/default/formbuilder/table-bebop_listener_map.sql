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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/formbuilder/table-bebop_listener_map.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

create table bebop_listener_map (
       component_id          integer
                             constraint bebop_listener_map_cid_fk
                             references bebop_components(component_id),
       listener_id           integer
                             constraint bebop_listener_map_lid_fk
                             references bebop_listeners(listener_id),
       constraint bebop_listener_map_un
       unique(component_id, listener_id)
);
