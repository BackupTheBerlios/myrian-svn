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
-- //enterprise/kernel/dev/kernel/sql/objects-create.sql 
--
-- @author oumi@arsdigita.com
-- @creation-date 2001-05-10
-- @cvs-id $Id: //core-platform/dev/sql/oracle-se/kernel/objects-create.sql#2 $
--

create sequence acs_object_id_seq cache 1000;

create table acs_objects (
	object_id		        integer not null
				            constraint acs_objects_pk primary key,
	object_type		        varchar2(100) not null,
    display_name            varchar2(200) not null,
    default_domain_class    varchar2(100)
);

create index acs_objects_object_type_idx on acs_objects(object_type);

create table object_container_map (
    object_id               integer not null
                            constraint aocm_object_id_fk
                            references acs_objects (object_id) 
                                on delete cascade
                            constraint aocm_object_id_pk primary key,
    container_id            integer not null
                            constraint aocm_container_id_fk
                            references acs_objects (object_id)
                                on delete cascade
) organization index;

create index ocm_container_object_idx on object_container_map (container_id, object_id);
