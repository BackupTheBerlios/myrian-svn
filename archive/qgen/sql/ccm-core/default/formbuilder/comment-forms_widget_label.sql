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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/formbuilder/comment-forms_widget_label.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

comment on table forms_widget_label is '
  This table maintains an association between a label and
  the widget it is labelling
';
comment on column forms_widget_label.label_id is '
  The unique id of the label.
';
comment on column forms_widget_label.widget_id is '
  The id of the widget being labelled.
';
