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
-- $Id: //core-platform/proto/sql/default/formbuilder/comment-bebop_form_sections.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $

comment on table bebop_form_sections is '
 This table contains some essential attributes particular to a Bebop 
 FormSection. 
';
comment on column bebop_form_sections.action is '
 This is the form HTML attribute action. You might think it should be stored in
 the attribute_string of the bebop_components table, and so do I. However,
 Bebop treats the action as a special case and doesnt store it as the other
 attributes.
';
