alter table cat_object_category_map add  index_p char(1)
                          constraint cat_obj_map_index_p_ck
                          check(index_p in ('0','1'));

