// this eliminates the table id since they are no longer needed
alter table cat_category_category_map drop column category_category_map_id;
alter table cat_object_category_map drop column object_category_map_id;
