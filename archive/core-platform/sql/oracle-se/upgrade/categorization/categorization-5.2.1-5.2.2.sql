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

alter table cat_categories add (default_ancestors varchar(4000));

create or replace procedure updateCategorizationTEMP
as 
   cursor categories is select category_id from cat_categories;
   v_category_id integer;
begin
   open categories;
   loop
      FETCH categories INTO v_category_id;
      EXIT WHEN categories%NOTFOUND;
      updateCategorizationHelpTEMP(v_category_id);
   end loop;
end;
/
show errors

create or replace procedure updateCategorizationHelpTEMP(v_category_id INTEGER)
as 
   cursor parents is 
                  select category_id 
                    from cat_category_category_map
                   where default_p = 1
                     and relation_type = 'child'
          connect by prior category_id = related_category_id
       start with related_category_id = v_category_id;
   parent_id integer;
begin
   update cat_categories set default_ancestors = category_id || '/'
          where category_id = v_category_id;
   open parents;
   loop
      FETCH parents INTO parent_id;
      EXIT WHEN parents%NOTFOUND;

      update cat_categories set default_ancestors = parent_id || '/' || default_ancestors where category_id = v_category_id;
   end loop;
end;
/
show errors

begin
  updateCategorizationTEMP;
end;
/

drop procedure updateCategorizationTEMP;
drop procedure updateCategorizationHelpTEMP;

alter table cat_categories modify (default_ancestors not null);


insert into acs_privileges (privilege) values ('map_to_category');