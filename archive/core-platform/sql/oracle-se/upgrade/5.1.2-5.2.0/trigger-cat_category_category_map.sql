--
-- Triggers to maintain denormalizations
--


create or replace trigger cat_categories_in_tr
after insert on cat_categories
for each row
begin
     hierarchy_add_item(:new.category_id, 'cat_cat_subcat_trans_index',
                        'category_id', 'subcategory_id');
end;
/
show errors


-- Subgroup triggers

create or replace trigger cat_category_cat_map_in_tr
after insert on cat_category_category_map
for each row
begin
     if (:new.relation_type = 'child') then
        hierarchy_add_subitem(:new.category_id, :new.related_category_id, 
                           'cat_cat_subcat_trans_index',
                           'category_id', 'subcategory_id');
     end if;
end;
/
show errors;



create or replace trigger cat_category_cat_map_del_tr
after delete on cat_category_category_map
for each row
begin
     hierarchy_remove_subitem(:old.category_id, :old.related_category_id,
                              'cat_cat_subcat_trans_index',      
                              'category_id', 'subcategory_id');
end;
/ 
show errors;



create or replace trigger cat_category_cat_map_up_tr
after update on cat_category_category_map
for each row
begin
  if (:old.relation_type = 'child') then
     hierarchy_remove_subitem(:old.category_id, :old.related_category_id,
                              'cat_cat_subcat_trans_index',      
                              'category_id', 'subcategory_id');
  end if;
  if (:new.relation_type = 'child') then
     hierarchy_add_subitem(:new.category_id, :new.related_category_id, 
                           'cat_cat_subcat_trans_index',
                           'category_id', 'subcategory_id');
  end if;
end;
/ 
show errors;


