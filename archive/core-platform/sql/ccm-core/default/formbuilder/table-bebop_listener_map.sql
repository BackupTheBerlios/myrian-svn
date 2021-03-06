--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/table-bebop_listener_map.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
