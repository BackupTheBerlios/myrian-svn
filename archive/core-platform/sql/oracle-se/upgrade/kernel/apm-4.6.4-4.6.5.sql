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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/apm-4.6.4-4.6.5.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


alter table apm_package_type_listener_map drop constraint apm_listener_map_pt_id_fk;

alter table apm_package_type_listener_map add constraint apm_listener_map_pt_id_fk foreign key(package_type_id) references apm_package_types on delete cascade;

alter table apm_packages drop constraint apm_packages_type_id_fk;

alter table apm_packages add constraint apm_packages_type_id_fk foreign key(package_type_id) references apm_package_types(package_type_id) on delete cascade;
 
alter table apm_package_type_listener_map modify (listener_id constraint apm_listener_map_list_id_nn not null);

alter table apm_package_type_listener_map modify (package_type_id constraint apm_listener_map_pt_id_nn not null);

drop table apm_package_type_map;
