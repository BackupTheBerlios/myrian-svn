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
-- @author Randy Graebner (randyg@alum.mit.edu)
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/persistence/persistence-4.6.4-4.6.5.sql#1 $

create table persistence_dynamic_ot (
    pdl_id                 integer
                           constraint persist_dynamic_ot_pdl_id_fk
                           references acs_objects on delete cascade
                           constraint persist_dynamic_ot_pdl_id_pk
                           primary key,
    pdl_file               clob not null,
    dynamic_object_type    varchar(700) 
                           constraint persist_dynamic_ot_dot_un
                           unique                            
);

