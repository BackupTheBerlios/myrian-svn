--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/default/formbuilder/table-bebop_meta_object.sql#4 $
-- $DateTime: 2003/08/15 13:46:34 $

create table bebop_meta_object (
    object_id integer
        constraint bebop_meta_obj_object_id_fk references
        acs_objects
        constraint bebop_meta_obj_object_id_pk primary key,
    type_id integer
        constraint bebop_meta_object_type_id_nn not null
        constraint bebop_meta_object_type_id_fk references
        bebop_object_type on delete cascade,
    pretty_name varchar(50),
    pretty_plural varchar(50),
    class_name varchar(200),
    props_form varchar(200),
    constraint bebop_meta_obj_un unique (type_id, class_name)
);
