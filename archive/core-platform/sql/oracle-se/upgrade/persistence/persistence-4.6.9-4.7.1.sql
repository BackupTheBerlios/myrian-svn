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

-- Data model for Persistence Dynamic Object Types
--

--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author Patrick McNeill (pmcneill@arsdigita.com)
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/persistence/persistence-4.6.9-4.7.1.sql#2 $

create table persistence_dynamic_assoc (
    pdl_id                 integer
                           constraint pers_dyn_assoc_pdl_id_fk
                           references acs_objects on delete cascade
                           constraint pers_dyn_assoc_pdl_id_pk
                           primary key,
    pdl_file               clob
                           constraint pers_dyn_assoc_pdl_file_nn
                           not null,
    model_name             varchar(200)
                           constraint pers_dyn_assoc_model_nn
                           not null,
    object_type_one        varchar(500)
                           constraint pers_dyn_assoc_object1_nn
                           not null,
    property_one           varchar(100)
                           constraint pers_dyn_assoc_prop1_nn
                           not null,
    object_type_two        varchar(500)
                           constraint pers_dyn_assoc_object2_nn
                           not null,
    property_two           varchar(100)
                           constraint pers_dyn_assoc_prop2_nn
                           not null,
    constraint pers_dyn_assoc_un
    unique (model_name, object_type_one, property_one, object_type_two,
            property_two)
);

