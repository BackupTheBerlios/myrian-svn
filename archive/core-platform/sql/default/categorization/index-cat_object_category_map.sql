-- foreign key index. Also needed to significantly reduce io when
-- looking for categories for an object
create index cat_obj_cat_map_object_id_idx on cat_object_category_map(object_id);
