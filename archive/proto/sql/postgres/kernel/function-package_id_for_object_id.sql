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
-- $Id: //core-platform/proto/sql/postgres/kernel/function-package_id_for_object_id.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

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
