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
-- $Id: //core-platform/test-packaging/sql/oracle-se/upgrade/5.1.2-5.2.0/table-cat_cat_subcat_trans_index.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $


create table cat_cat_subcat_trans_index (
      category_id   integer
                    constraint cat_cat_subcat_index_c_nn 
                    not null
                    constraint cat_cat_subcat_index_c_fk
                    references cat_categories(category_id) on delete cascade, 
      subcategory_id integer
                    constraint cat_cat_subcat_index_s_nn 
                    not null
                    constraint cat_cat_subcat_index_s_fk
                    references cat_categories(category_id) on delete cascade, 
      n_paths       integer 
                    constraint cat_cat_subcat_index_n_nn
                    not null,
      constraint cat_cat_subcat_index_pk primary key(category_id, subcategory_id),
      -- This prevents circularity in the category-subcategory graph.
      -- If group_id=subgroup_id then n_paths=0.
	  constraint cat_subcat_circularity_ck 
                 check ( category_id != subcategory_id or n_paths=0 ),
      -- This constraint makes sure that we never forget to delete rows when
      -- we decrement n_paths.  n_paths should never reach 0 except for
      -- mappings where group_id=subgroup_id (in which case n_paths should
      -- always be 0 due to above constraint).
      constraint cat_cat_subcat_n_paths_ck
                 check (n_paths>0 or category_id=subcategory_id)
);
