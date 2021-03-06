--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/kernel/package-hierarchy_denormalization.sql#2 $
-- $DateTime: 2004/01/29 12:35:08 $

create or replace procedure hierarchy_add_item (v_item_id in INTEGER, 
                                                v_table in VARCHAR, 
                                                v_itemColumn in VARCHAR, 
                                                v_subitemColumn in VARCHAR)
is 
begin
    execute immediate 'insert into ' || v_table || ' ( ' || v_itemColumn || 
                      ', ' || v_subitemColumn || ', n_paths) values 
                      (' || v_item_id || ', ' || v_item_id || ', 0)';
end;
/



create or replace procedure hierarchy_add_subitem (v_item_id in INTEGER,
                                                   v_subitem_id in INTEGER,
                                                   v_table in VARCHAR,
                                                   v_itemColumn in VARCHAR, 
                                                   v_subitemColumn in VARCHAR)
  is 
    TYPE itemCursor IS REF CURSOR;
    v_path_increment integer;
    newEntry itemCursor;
    sql_stmt VARCHAR(4000);
    v_select_item_id integer;
    v_select_subitem_id integer;
    v_select_ancestor_n_paths integer;
    v_select_descendant_n_paths integer;
  begin
      sql_stmt := 'select ancestors.' || v_itemColumn || ' as item_id, 
                   descendants.' || v_subitemColumn || ' as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from ' || v_table || ' ancestors,
               ' || v_table || ' descendants
          where ancestors.' || v_subitemColumn || ' = ' || v_item_id || '
            and descendants.'|| v_itemColumn || ' = ' || v_subitem_id;

      OPEN newEntry FOR sql_stmt;
      LOOP
        FETCH newEntry INTO v_select_item_id, v_select_subitem_id, v_select_ancestor_n_paths, v_select_descendant_n_paths;
        EXIT WHEN newEntry%NOTFOUND;

          v_path_increment :=
              v_select_ancestor_n_paths * v_select_descendant_n_paths;

          execute immediate 'update ' || v_table ||  '
          set n_paths = n_paths + ' || v_path_increment || '
          where ' || v_itemColumn || ' = ' || v_select_item_id || '
            and ' || v_subitemColumn || ' = ' || v_select_subitem_id;

          if (SQL%NOTFOUND) then
              execute immediate 'insert into ' || v_table || '
              (' || v_itemColumn || ',' || v_subitemColumn || ', n_paths)
              values
              (' || v_select_item_id || ',' || v_select_subitem_id || ', ' ||
              v_path_increment || ')';
          end if;

      END LOOP;
      CLOSE newEntry;
 end;
/




create or replace procedure hierarchy_remove_subitem (v_item_id in INTEGER,
                                                   v_subitem_id in INTEGER,
                                                   v_table in VARCHAR,
                                                   v_itemColumn in VARCHAR, 
                                                   v_subitemColumn in VARCHAR)
  is 
    TYPE itemCursor IS REF CURSOR;
    v_path_decrement integer;
    newEntry itemCursor;
    sql_stmt VARCHAR(4000);
    v_select_item_id integer;
    v_select_subitem_id integer;
    v_select_ancestor_n_paths integer;
    v_select_descendant_n_paths integer;
  begin
      sql_stmt := 'select ancestors.' || v_itemColumn || ' as item_id, 
                   descendants.' || v_subitemColumn || ' as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from ' || v_table || ' ancestors,
               ' || v_table || ' descendants
          where ancestors.' || v_subitemColumn || ' = ' || v_item_id || '
            and descendants.'|| v_itemColumn || ' = ' || v_subitem_id;

      OPEN newEntry FOR sql_stmt;
      LOOP
        FETCH newEntry INTO v_select_item_id, v_select_subitem_id, v_select_ancestor_n_paths, v_select_descendant_n_paths;
        EXIT WHEN newEntry%NOTFOUND;

        v_path_decrement :=
            v_select_ancestor_n_paths * v_select_descendant_n_paths;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        execute immediate 'delete from ' || v_table || '
        where ' || v_itemColumn || ' = ' || v_select_item_id || '
          and ' || v_subitemColumn || ' = ' || v_select_subitem_id || '
          and n_paths <= ' || v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then
           execute immediate 'update ' || v_table || '
              set n_paths = n_paths - ' || v_path_decrement || '
            where ' || v_itemColumn || ' = ' || v_select_item_id || '
              and ' || v_subitemColumn || ' = ' || v_select_subitem_id;
        end if;
      END LOOP;
      CLOSE newEntry;
 end;
/
