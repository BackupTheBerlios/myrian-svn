create or replace function upgrade_dnm_context ()
  returns integer as '
  declare
    c record;
  begin

    for c in select object_id from acs_objects where object_id != 0  loop
      perform dnm_context_add_object(c.object_id, 0);
    end loop;

    for c in select object_id, context_id 
               from object_context where object_id != 0 and context_id is not null and context_id != 0 loop
      perform dnm_context_change_context(c.object_id, c.context_id);
    end loop;

    for c in select object_id from acs_permissions loop
      perform dnm_context_add_grant(c.object_id);
    end loop;

    return null;
end; ' language 'plpgsql'
;

select upgrade_dnm_context();
