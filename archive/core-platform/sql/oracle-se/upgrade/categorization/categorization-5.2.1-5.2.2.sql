// this eliminates the table id since they are no longer needed
alter table cat_category_category_map drop column category_category_map_id;
alter table cat_object_category_map drop column object_category_map_id;

@@ ../../../default/categorization/table-cat_cat_subcat_trans_index.sql
@@ ../../../default/kernel/package-hierarchy_denormalization.sql
@@ ../../../default/categorization/trigger-cat_category_category_map.sql

-- we now populated the denormalization tables
insert into cat_cat_subcat_trans_index (category_id, subcategory_id, n_paths) 
select category_id, category_id, 0 from cat_categories;

declare
begin
   for new_entry in (
       select category_id, related_category_id 
       from cat_category_category_map where relation_type = 'child'
   ) loop
        hierarchy_add_subitem(new_entry.category_id, 
                              new_entry.related_category_id, 
                           'cat_cat_subcat_trans_index',
                           'category_id', 'subcategory_id');
   end loop;

end;
/
show errors;