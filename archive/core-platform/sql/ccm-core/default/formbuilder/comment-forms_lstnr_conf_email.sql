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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/comment-forms_lstnr_conf_email.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

comment on table forms_lstnr_conf_email is '
  Stores details of the confirmation email to be sent
  upon submission.
';
comment on column forms_lstnr_conf_email.sender is '
  Email address of sender for confirmation email
';
comment on column forms_lstnr_conf_email.subject is '
  The subject line of the mail
';
comment on column forms_lstnr_conf_email.body is '
  The text of the email optionally containing
  placeholders of the form "::foo.bar::"
';
