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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/workflow/workflow-4.6.7-4.6.8.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


-- Upgrades the data model from version 4.6.7 to 4.6.8
--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
--
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/workflow/workflow-4.6.7-4.6.8.sql#1 $

create index cw_tasks_parent_task_id_idx on cw_tasks(parent_task_id);
create index cw_processes_object_id_idx on cw_processes(object_id);
create index cw_processes_process_def_idx on cw_processes(process_def_id);

-- Add on delete cascade constraint to the processes table to
-- facilitate deleting a referenced object.
--
-- @author <a href="mailto:ron@arsdigita.com">Ron Henderson</a>

alter table cw_processes drop constraint processes_object_ref;
alter table cw_processes add ( 
    constraint processes_object_fk foreign key (object_id) 
               references acs_objects on delete cascade 
);