  create or replace function permissions_rebuild()
  returns integer
  as
  '
   declare
    row    record;
   begin
    delete from object_context_map;
    delete from granted_context_non_leaf_map;
    delete from ungranted_context_non_leaf_map;
    delete from object_grants;

    for row in select * from object_context
                where context_id is not null 
    loop
      perform permissions_add_context(row.object_id, row.context_id);
    end loop;

    for row in select * from acs_permissions 
    loop
      perform permissions_add_grant(row.object_id);
    end loop;
    return 1;
  end;' language 'plpgsql';