--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/postgres/categorization/trigger-cat_category_category_map.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $


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



