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
-- $Id: //core-platform/test-packaging/sql/oracle-se/upgrade/5.1.2-5.2.0/add-applications.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

create table applications (
    application_id INTEGER not null
        constraint applicati_applicati_id_p_ogstm
          primary key,
        -- referential constraint for application_id deferred due to circular dependencies
    application_type_id INTEGER not null,
        -- referential constraint for application_type_id deferred due to circular dependencies
    cell_number INTEGER,
    description VARCHAR(4000),
    package_id INTEGER,
        -- referential constraint for package_id deferred due to circular dependencies
    parent_application_id INTEGER,
        -- referential constraint for parent_application_id deferred due to circular dependencies
    primary_url VARCHAR(4000),
    sort_key INTEGER,
    timestamp DATE not null,
    title VARCHAR(200)
);

create table application_types (
    application_type_id INTEGER not null
        constraint appli_typ_appli_typ_id_p_r5e8o
          primary key,
    description VARCHAR(4000),
    has_embedded_view_p CHAR(1),
    has_full_page_view_p CHAR(1),
    object_type VARCHAR(100) not null
        constraint applicat_typ_obje_type_u_pf2uk
          unique,
    package_type_id INTEGER,
        -- referential constraint for package_type_id deferred due to circular dependencies
    profile VARCHAR(20),
    provider_id INTEGER,
        -- referential constraint for provider_id deferred due to circular dependencies
    singleton_p CHAR(1),
    title VARCHAR(200),
    workspace_application_p CHAR(1)
);

create table application_type_privilege_map (
    privilege VARCHAR(100) not null,
        -- referential constraint for privilege deferred due to circular dependencies
    application_type_id INTEGER not null,
        -- referential constraint for application_type_id deferred due to circular dependencies
    constraint appl_typ_pri_map_app_t_p_dc1jg
      primary key(application_type_id, privilege)
);

alter table application_type_privilege_map add 
    constraint appl_typ_pri_map_app_t_f_kgrfj foreign key (application_type_id)
      references application_types(application_type_id) on delete cascade;
alter table application_type_privilege_map add 
    constraint appl_typ_pri_map_privi_f_s3pwb foreign key (privilege)
      references acs_privileges(privilege) on delete cascade;
alter table application_types add 
    constraint applica_typ_pac_typ_id_f_v80ma foreign key (package_type_id)
      references apm_package_types(package_type_id);
alter table application_types add 
    constraint applicat_typ_provid_id_f_bm274 foreign key (provider_id)
      references application_types(application_type_id);
alter table applications add 
    constraint applica_applica_typ_id_f_k2bi3 foreign key (application_type_id)
      references application_types(application_type_id);
alter table applications add 
    constraint applica_par_applica_id_f_hvxh7 foreign key (parent_application_id)
      references applications(application_id);
alter table applications add 
    constraint applicati_applicati_id_f_a35g2 foreign key (application_id)
      references acs_objects(object_id) on delete cascade;
alter table applications add 
    constraint application_package_id_f_cdaho foreign key (package_id)
      references apm_packages(package_id);
