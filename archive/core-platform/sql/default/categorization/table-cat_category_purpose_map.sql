create table cat_category_purpose_map (
  category_id             integer
                          constraint cat_cat_pur_map_cat_id_fk
                          references cat_categories on delete cascade,
  purpose_id              integer
                          constraint cat_obj_map_purpose_id_fk
                          references cat_purposes on delete cascade,
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id)
);
