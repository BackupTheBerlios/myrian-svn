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
-- $Id: //core-platform/proto/sql/default/workflow/table-cw_processes.sql#5 $
-- $DateTime: 2003/08/04 16:15:53 $

create table cw_processes (
  process_id             integer 
                         constraint processes_pk
                         primary key
               		 constraint process_task_id_fk 
               		 references cw_tasks,
  process_def_id         integer
               		 constraint process_process_def_id_fk 
               		 references cw_tasks on delete cascade,
  process_state          varchar(16)
                         constraint process_state_ck
                         check (process_state in ('stopped', 'started','deleted','init')),
  object_id              integer
                         constraint processes_object_fk
                         references acs_objects
);
