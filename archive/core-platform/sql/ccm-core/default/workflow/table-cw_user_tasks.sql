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
-- $Id: //core-platform/dev/sql/ccm-core/default/workflow/table-cw_user_tasks.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table cw_user_tasks (
  task_id                integer
                         constraint user_tasks_task_id_pk primary key
                         constraint user_tasks_task_id_fk references cw_tasks,
  is_locked              char(1) default 'f'
                         constraint task_is_locked_ck
                         check (is_locked in ('t', 'f')),
  locking_user_id        integer,
  --     use constraint when using users table
  --                         constraint user_tasks_locking_user_id_fk references users,
  start_date             timestamptz,
  due_date               timestamptz,
  duration_minutes       integer ,
  notification_sender_id integer 
);
