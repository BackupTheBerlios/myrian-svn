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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/comment-bebop_form_process_listeners.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

comment on table bebop_form_process_listeners is '
  This table maps process listeners to individual forms.
';
comment on column bebop_form_process_listeners.form_section_id is '
  The oid of the persistent form object.
';
comment on column bebop_form_process_listeners.listener_id is '
  The oid of the process listener object.
';
comment on column bebop_form_process_listeners.position is '
  Stores the position in which the listener was added to the
  form section.
';
