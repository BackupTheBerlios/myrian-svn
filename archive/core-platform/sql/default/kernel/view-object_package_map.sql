create or replace view object_package_map as
select o.object_id, p.package_id
from acs_objects o, apm_packages p
where p.package_id=o.object_id 
   or p.package_id in (select container_id
                       from object_container_map
                       start with object_id = o.object_id
                       connect by prior container_id = object_id);
