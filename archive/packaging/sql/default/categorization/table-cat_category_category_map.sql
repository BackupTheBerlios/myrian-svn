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
-- $Id: //core-platform/test-packaging/sql/default/categorization/table-cat_category_category_map.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table cat_category_category_map (
  category_id             integer
                          constraint cat_cat_map_parent_id_fk
                          references cat_categories,
  related_category_id     integer
                          constraint cat_cat_map_category_id_fk
                          references cat_categories,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_cat_map_default_p_ck
                          check(default_p in ('0','1')),
  sort_key                integer,
  relation_type           varchar(10) 
                          constraint cat_cat_map_rel_type_ck
                          check(relation_type in ('child','related')),
  constraint cat_cat_cat_map_ckone
  check(not category_id = related_category_id),
  constraint cat_cat_catmap_un
  unique(category_id, related_category_id)
);
