--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/table-forms_lstnr_conf_redirect.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table forms_lstnr_conf_redirect (
    listener_id integer
        constraint forms_lstnr_conf_redirect_fk references
        bebop_process_listeners
        constraint forms_lstnr_conf_redirect_pk primary key,
    url varchar(160)
);
