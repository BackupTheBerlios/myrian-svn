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
-- $Id: //core-platform/test-packaging/sql/oracle-se/formbuilder/widgets-drop.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $


drop sequence forms_unique_id_seq;

drop table forms_dd_select;
drop table forms_dataquery;

drop table forms_lstnr_xml_email;
drop table forms_lstnr_tmpl_email;
drop table forms_lstnr_simple_email;
drop table forms_lstnr_conf_redirect;
drop table forms_lstnr_conf_email;

drop table forms_widget_label;
