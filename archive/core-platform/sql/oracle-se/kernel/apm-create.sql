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
-- /acs-core/kernel/dev/kernel/sql/apm-create.sql
--
-- Data model for the ACS Package Manager (APM)
--
-- @author Bryan Quinn (bquinn@arsdigita.com)
-- @creation-date 2001/05/16
-- @version-id $Id: //core-platform/dev/sql/oracle-se/kernel/apm-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $

-----------------------------
--     PACKAGE OBJECT      --
-----------------------------

-----------------------------
--     Knowledge Level    --
-----------------------------

create table apm_package_types (
    package_type_id             integer 
                                constraint apm_package_types_pk primary key,
    package_key                 varchar2(100)
                                constraint apm_package_types_key_nn not null
                                constraint apm_package_types_key_un unique,
    pretty_name                 varchar2(100)
                                constraint apm_package_types_pretty_n_nn not null
                                constraint apm_package_types_pretty_n_un unique,
    pretty_plural               varchar2(100)
                                constraint apm_package_types_pretty_pl_un unique,
    package_uri                 varchar2(1500)
                                constraint apm_packages_types_p_uri_nn not null
                                constraint apm_packages_types_p_uri_un unique,
    servlet_package             varchar2(100),
    dispatcher_class            varchar2(100) 
        default 'com.arsdigita.dispatcher.JSPApplicationDispatcher'
);

create table apm_listeners (
    listener_id                 integer
                                constraint apm_listeners_pk
                                primary key,
    listener_class              varchar2(100)
                                constraint apm_listeners_class_nn
                                not null
                                constraint apm_listeners_class_un
                                unique
);

create table apm_package_type_listener_map (
    package_type_id             integer
                                constraint apm_listener_map_pt_id_fk
                                references apm_package_types
                                on delete cascade
                                constraint apm_listener_map_pt_id_nn
                                not null,    
    listener_id                 integer
                                constraint apm_listener_map_list_id_fk
                                references apm_listeners
                                on delete cascade
                                constraint apm_listener_map_list_id_nn
                                not null,
    constraint apm_listener_map_id_class_un unique
                (package_type_id, listener_id) 
);                                

create table apm_packages (
    package_id                  constraint apm_packages_package_id_fk
                                references acs_objects(object_id)
                                constraint apm_packages_pack_id_pk primary key,
    package_type_id             constraint apm_packages_type_id_fk
                                references apm_package_types(package_type_id)
                                on delete cascade,
    pretty_name                 varchar2(300),
    locale_id                   constraint apm_packages_locale_id_fk
                                references g11n_locales (locale_id)
);

create index apm_packages_locale_id_idx on apm_packages(locale_id);
create index apm_packages_package_type_idx on apm_packages(package_type_id);

create or replace view object_package_map as
select o.object_id, p.package_id
from acs_objects o, apm_packages p
where p.package_id=o.object_id 
   or p.package_id in (select container_id
                       from object_container_map
                       start with object_id = o.object_id
                       connect by prior container_id = object_id);

