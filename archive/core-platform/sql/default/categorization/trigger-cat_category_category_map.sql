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
-- $Id: //core-platform/dev/sql/default/categorization/trigger-cat_category_category_map.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

-- Triggers to maintain denormalizations

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


