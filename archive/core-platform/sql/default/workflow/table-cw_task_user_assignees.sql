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
-- $Id: //core-platform/dev/sql/default/workflow/table-cw_task_user_assignees.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

create table cw_task_user_assignees (
  -- 
  -- task_id references cw_user_tasks.task_id for the default assignees
  -- 
  task_id		 integer
			 constraint task_user_task_id_nn not null
	                 references cw_user_tasks(task_id) 
                         on delete cascade,
  user_id                integer
               		 constraint user_task_id_nn not null
	                 constraint user_task_id_fk 
                             references users(user_id) on delete cascade,
  constraint task_user_assignees_pk
  primary key (task_id, user_id)  
);
