create table cat_object_category_map (
  category_id             integer
                          constraint cat_obj_cat_map_cat_id_fk
                          references cat_categories on delete cascade,
  object_id               integer
                          constraint cat_obj_map_object_id_fk
                          references acs_objects on delete cascade,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_obj_map_default_p_ck
                          check(default_p in ('0','1')),
  sort_key                integer,
  constraint cat_obj_cat_map_ckone
  check(not category_id = object_id),
  constraint cat_obj_cat_map_un
  unique(category_id, object_id)
);
