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
-- $Id: //core-platform/dev/sql/default/formbuilder/table-forms_dataquery.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

create table forms_dataquery (
    query_id integer 
        constraint forms_dq_query_id_pk primary key
        constraint forms_dq_query_id_fk 
        references acs_objects (object_id) on delete cascade,
    type_id integer
        constraint forms_dq_query_type_id_fk references
        bebop_object_type on delete cascade,
    description varchar(200),
    name varchar(60),
    constraint forms_dataquery_un unique (type_id, name)
);
