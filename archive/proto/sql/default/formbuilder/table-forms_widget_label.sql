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
-- $Id: //core-platform/proto/sql/default/formbuilder/table-forms_widget_label.sql#5 $
-- $DateTime: 2003/08/04 16:15:53 $

create table forms_widget_label (
    label_id integer
        constraint forms_wgt_label_label_id_fk
        references bebop_widgets (widget_id)
        constraint forms_wgt_label_label_id_pk primary key,
    widget_id integer
        constraint forms_wgt_label_widget_id_fk
        references bebop_widgets (widget_id) on delete cascade
);
