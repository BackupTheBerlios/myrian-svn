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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/formbuilder/table-bebop_object_type.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

create table bebop_object_type (
    type_id integer
        constraint bebop_object_type_type_id_fk references
        acs_objects (object_id)
        constraint bebop_object_type_type_id_pk primary key,
    app_type varchar(20)
        constraint bebop_object_type_app_nn not null,
    class_name varchar(120)
        constraint bebop_object_type_class_nn not null,
    constraint bebop_object_type_un unique(app_type, class_name)
);
