--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/places/pl-core-4.6.5-4.6.6.sql#1 $
-- $DateTime: 2002/10/24 11:07:46 $


--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/proto/sql/oracle-se/upgrade/places/pl-core-4.6.5-4.6.6.sql#1 $ by $Author: dennis $, $DateTime: 2002/10/24 11:07:46 $
--

drop table location_map;

alter table places add
    place_name varchar2(200) constraint places_place_name_nn not null;

alter table place_hierarchy drop constraint place_hierarachy_pk;
alter table place_hierarchy
    add constraint
    place_hierarchy_pk primary key (child_id);

drop index place_hierarchy_idx;

create unique index place_hierarchy_un_idx on
    place_hierarchy (parent_id, child_id);

create table place_hierarchy_tc (
    child_id                    constraint place_hier_tc_child_id_fk
                                references places (place_id)
                                constraint place_hierarchy_index_pk
                                primary key,
    parent_id                   constraint place_hier_tc_parent_id_fk
                                references places (place_id),
    depth                       integer default 0
                                constraint place_hier_tc_depth_nn
                                not null
) organization index;

create unique index place_hierarchy_tc_un_idx on
    place_hierarchy_tc (parent_id, child_id);
