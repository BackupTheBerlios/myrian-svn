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
-- $Id: //core-platform/dev/sql/default/formbuilder/comment-bebop_process_listeners.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

comment on table bebop_process_listeners is '
  This table contains the persistent data for process listeners
';
comment on column bebop_process_listeners.listener_id is '
  This is the unique object id for this process listener.
';
comment on column bebop_process_listeners.name is '
  The user supplied name of this form process listener.
';
comment on column bebop_process_listeners.description is '
  The user supplied, long description of the purpose of the
  process listener
';
