--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
--
-- @author bquinn@arsdigita.com
-- @version-id $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/apm-4.6.4-4.6.5.sql#1 $
--
-- Add on delete cascade and not null constraints to the package tables.
-- Remove the unused apm_package_type_map.

alter table apm_package_type_listener_map drop constraint apm_listener_map_pt_id_fk;

alter table apm_package_type_listener_map add constraint apm_listener_map_pt_id_fk foreign key(package_type_id) references apm_package_types on delete cascade;

alter table apm_packages drop constraint apm_packages_type_id_fk;

alter table apm_packages add constraint apm_packages_type_id_fk foreign key(package_type_id) references apm_package_types(package_type_id) on delete cascade;
 
alter table apm_package_type_listener_map modify (listener_id constraint apm_listener_map_list_id_nn not null);

alter table apm_package_type_listener_map modify (package_type_id constraint apm_listener_map_pt_id_nn not null);

drop table apm_package_type_map;
