--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- Create places core.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/places/pl-core-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $
--

create table places (
    place_id                    integer
                                constraint places_pk
                                primary key,
    place_name                  varchar2(200)
                                constraint places_place_name_nn
                                not null,
    latitude                    number(9,6),
    longitude                   number(9,6)
);

create table place_hierarchy (
    child_id                    constraint place_hierarchy_child_id_fk
                                references places (place_id)
                                constraint place_hierarchy_pk
                                primary key,
    parent_id                   constraint place_hierarchy_parent_id_fk
                                references places (place_id)
);

create unique index place_hierarchy_un_idx on
    place_hierarchy (parent_id, child_id);

-- denormalize the above hierarchy for speedier access. so if the hierarchy
-- contains the following rows:
--
-- parent       child
--   A            B
--   A            C
--   C            D
--
-- then this denormalization will contain the following rows:
--
-- parent       child
--   A            B
--   A            C
--   C            D
--   A            D
--
-- for those who care, this is called "transitive closure."
--
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

-- maintain the above denormalization
-- create or replace trigger place_hierarchy_tc_i_tr
-- after insert on place_hierarchy
-- for each row
-- begin
-- end;
-- 
-- create or replace trigger place_hierarchy_tc_u_tr
-- after update on place_hierarchy
-- for each row
-- begin
-- end;
-- 
-- create or replace trigger place_hierarchy_tc_d_tr
-- after delete on place_hierarchy
-- for each row
-- begin
-- end;
