--
-- Triggers to maintain denormalizations
--

create or replace function cat_categories_in_fn () returns opaque as '
begin
  perform hierarchy_add_item(new.category_id, ''cat_cat_subcat_trans_index'',
                             ''category_id'', ''subcategory_id'');
  return null;
end;' language 'plpgsql';

create trigger cat_categories_in_tr
after insert on cat_categories
for each row execute procedure
cat_categories_in_fn();


-- Subgroup triggers


create or replace function cat_cat_subcat_in_fn () returns opaque as '
begin
  if (new.relation_type = ''child'') then
  perform hierarchy_add_subitem(new.category_id, new.related_category_id, 
                                ''cat_cat_subcat_trans_index'',              
                                ''category_id'', ''subcategory_id'');
  end if;
  return null;
end;' language 'plpgsql';

create trigger cat_category_subcategory_in_tr
after insert on cat_category_category_map
for each row execute procedure
cat_cat_subcat_in_fn();



create or replace function cat_cat_subcat_del_fn () returns opaque as '
begin
  if (old.relation_type = ''child'') then 
     perform hierarchy_remove_subitem(old.category_id, old.related_category_id,
                                ''cat_cat_subcat_trans_index'',              
                                ''category_id'', ''subcategory_id'');
  end if;
  return null;
end;' language 'plpgsql';

create trigger cat_category_subcategory_del_tr
after delete on cat_category_category_map
for each row execute procedure
cat_cat_subcat_del_fn();



create or replace function cat_cat_subcat_up_fn () returns opaque as '
begin
  if (old.relation_type = ''child'') then 
     perform hierarchy_remove_subitem(old.category_id, old.related_category_id,
                                ''cat_cat_subcat_trans_index'',              
                                ''category_id'', ''subcategory_id'');
  end if;
  if (new.relation_type = ''child'') then
     perform hierarchy_add_subitem(new.category_id, new.related_category_id, 
                                ''cat_cat_subcat_trans_index'',              
                                ''category_id'', ''subcategory_id'');
  end if;
  return null;
end;' language 'plpgsql';

create trigger cat_category_subcategory_up_tr
after update on cat_category_category_map
for each row execute procedure
cat_cat_subcat_up_fn();



