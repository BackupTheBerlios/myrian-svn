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
-- $Id: //core-platform/proto/sql/default/formbuilder/table-bebop_form_process_listeners.sql#5 $
-- $DateTime: 2003/08/04 16:15:53 $

create table bebop_form_process_listeners (
        form_section_id         integer
                                constraint bebop_form_process_lstnr_fs_fk
                                references bebop_form_sections,
        listener_id             integer
                                constraint bebop_form_process_lstnr_li_fk
                                references bebop_process_listeners,
        position                integer,
        constraint bebop_form_process_lstnr_pk
        primary key (form_section_id, listener_id),
        constraint bebop_form_process_lstnr_un
	unique (form_section_id, position)
);
