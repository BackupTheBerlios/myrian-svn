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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/kernel/table-context_child_counts.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


-- This table has been removed because it causes contention when
-- inserting two objects that share the same context. Both try to
-- update this table and so the operations become serialized. This is
-- temporarily replaced with a select from object_context_map, but
-- since I believe we want to eventually remove object_context_map in
-- favor of something that doesn't store the mapping for leaf nodes,
-- I'm leaving this here.

--create table context_child_counts (
--   object_id  integer not null
--              constraint ccc_object_id_fk
--              references acs_objects (object_id)
--              constraint ccc_object_id_pk
--              primary key,
--   n_children integer default 1 not null
--              constraint ccc_n_children_ck
--              check (n_children>=1)
--);

-- XXX organization index;
