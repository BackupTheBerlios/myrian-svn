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
-- $Id: //core-platform/dev/sql/oracle-se/workflow/workflow-drop.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


drop table cw_task_group_assignees;
drop table cw_task_user_assignees;
drop table cw_task_listeners ;
drop table cw_task_comments;
drop table cw_task_dependencies ;
drop table cw_process_definitions;
drop table cw_process_task_map;
drop table cw_processes;
drop table cw_system_tasks;
drop table cw_user_tasks;
drop table cw_tasks;
drop sequence cw_sequences;
