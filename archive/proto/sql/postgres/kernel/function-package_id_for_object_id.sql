create or replace function package_id_for_object_id (integer)
  returns integer as '
  declare
    v_object_id alias for $1;
    v_package_id integer;
    v_container_id integer;
    v_count integer;
  begin

    select package_id into v_package_id 
    from apm_packages
    where package_id = v_object_id;

    if (FOUND) then
       return v_package_id;
    end if;

    select container_id into v_container_id
    from object_container_map
    where object_id = v_object_id;

    if (NOT FOUND) then
        return null;
    end if;

    select package_id_for_object_id(v_container_id) 
    into v_container_id from dual;

    return v_container_id;
end;' language 'plpgsql';
