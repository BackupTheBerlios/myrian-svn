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
-- $Id: //core-platform/test-packaging/sql/default/formbuilder/table-forms_dd_select.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

create table forms_dd_select (
    widget_id integer 
        constraint forms_dds_widget_id_pk primary key
        constraint forms_dds_widget_id_fk 
        references bebop_widgets (widget_id),
    multiple_p char(1) check (multiple_p in ('1', '0')),
    query_id integer
        constraint forms_dds_query_id_fk
        references forms_dataquery (query_id) on delete cascade
);
