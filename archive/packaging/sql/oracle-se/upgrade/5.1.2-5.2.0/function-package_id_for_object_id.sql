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
-- $Id: //core-platform/test-packaging/sql/oracle-se/upgrade/5.1.2-5.2.0/function-package_id_for_object_id.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

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
