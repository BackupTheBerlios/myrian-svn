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
-- $Id: //core-platform/dev/sql/default/workflow/table-cw_task_dependencies.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

create table cw_task_dependencies (
  task_id  		 integer
               		 constraint task_dep_task_id_nn not null
               		 constraint task_dep_task_id_fk 
               		 references cw_tasks
	                 on delete cascade,
  dependent_task_id      integer
               		 constraint task_dep_id_nn not null
                         constraint task_def_id_fk 
                         references cw_tasks
	                 on delete cascade,
  constraint task_dependencies_pk
  primary key (task_id, dependent_task_id)  
);
