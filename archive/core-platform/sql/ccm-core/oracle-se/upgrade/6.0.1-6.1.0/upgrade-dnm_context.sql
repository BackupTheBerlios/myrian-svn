begin

  for c in (select object_id from acs_objects where object_id != 0 ) loop
    dnm_context.add_object(c.object_id, 0);
  end loop;

  for c in (select object_id, context_id 
              from object_context where object_id != 0 and context_id is not null and context_id != 0) loop
    dnm_context.change_context(c.object_id, c.context_id);
  end loop;

  for c in (select object_id from acs_permissions) loop
    dnm_context.add_grant(c.object_id);
  end loop;
end;
/
