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
-- $Id: //core-platform/proto/sql/default/formbuilder/table-forms_lstnr_xml_email.sql#4 $
-- $DateTime: 2003/08/04 15:56:00 $

create table forms_lstnr_xml_email (
    listener_id integer
        constraint forms_lstnr_xml_email_fk references
        bebop_process_listeners
        constraint forms_lstnr_xml_email_pk primary key,
    recipient varchar(120),
    subject varchar(120)
);
