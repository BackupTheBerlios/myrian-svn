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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/formbuilder/table-bebop_form_sections.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

create table bebop_form_sections (
       form_section_id           integer
                                 constraint bebop_form_sections_id_fk
                                 references bebop_components (component_id)
                                 constraint bebop_form_sections_pk
                                 primary key,
       action                    varchar(500)
);
