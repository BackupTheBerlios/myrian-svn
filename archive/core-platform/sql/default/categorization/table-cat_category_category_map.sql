create table cat_category_category_map (
  category_id             integer
                          constraint cat_cat_map_parent_id_fk
                          references cat_categories on delete cascade,
  related_category_id     integer
                          constraint cat_cat_map_category_id_fk
                          references cat_categories on delete cascade,
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
