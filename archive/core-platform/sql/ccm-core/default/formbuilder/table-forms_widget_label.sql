--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/table-forms_widget_label.sql#3 $
-- $DateTime: 2004/03/30 17:47:27 $

create table forms_widget_label (
    label_id integer
        constraint forms_wgt_label_label_id_fk
        references bebop_components (component_id)
        constraint forms_wgt_label_label_id_pk primary key,
    widget_id integer
        constraint forms_wgt_label_widget_id_fk
        references bebop_widgets (widget_id) on delete cascade
);
