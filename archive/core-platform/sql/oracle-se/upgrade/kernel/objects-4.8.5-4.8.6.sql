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
-- Data model upgrade from 4.8.5 to 4.8.6
--
-- Copyright (C) 2001 Arsdigita Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/objects-4.8.5-4.8.6.sql#1 $

alter table object_container_map
    drop constraint aocm_object_id_fk;

alter table object_container_map
    drop constraint aocm_container_id_fk;

alter table object_container_map
    add constraint aocm_object_id_fk
        foreign key (object_id) 
        references acs_objects (object_id) 
        on delete cascade;

alter table object_container_map
    add constraint aocm_container_id_fk
        foreign key (container_id) 
        references acs_objects (object_id) 
        on delete cascade;
