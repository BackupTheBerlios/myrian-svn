----------------------
-- DENORMALIZATIONS --
----------------------

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