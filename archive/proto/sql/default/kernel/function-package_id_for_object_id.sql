create or replace function package_id_for_object_id(v_object_id INTEGER)
return INTEGER
as
    v_package_id apm_packages.package_id%TYPE;
    cursor containers is (select package_id from apm_packages
        where package_id in (select container_id 
        from object_container_map
        start with object_id = v_object_id
        connect by prior container_id = object_id 
        union select v_object_id from dual));
begin
    open containers;
    fetch containers into v_package_id;
    if (containers%NOTFOUND) then
       return null;
    else 
       return v_package_id;
    end if;
end;
/ 
show errors;
