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
-- $Id: //core-platform/test-packaging/sql/default/categorization/table-cat_object_category_map.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table cat_object_category_map (
  category_id             integer
                          constraint cat_obj_cat_map_cat_id_fk
                          references cat_categories,
  object_id               integer
                          constraint cat_obj_map_object_id_fk
                          references acs_objects,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_obj_map_default_p_ck
                          check(default_p in ('0','1')),
  index_p                 char(1)
                          constraint cat_obj_map_index_p_ck
                          check(index_p in ('0','1')),
  sort_key                integer,
  constraint cat_obj_cat_map_ckone
  check(not category_id = object_id),
  constraint cat_obj_cat_map_un
  unique(category_id, object_id)
);
