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
-- $Id: //core-platform/test-packaging/sql/default/workflow/table-cw_system_tasks.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table cw_system_tasks (
  task_id                integer
			 constraint system_tasks_task_id_pk primary key
                         constraint system_tasks_task_id_fk references cw_tasks
                                    on delete cascade
);
