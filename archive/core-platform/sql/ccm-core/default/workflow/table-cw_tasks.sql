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
-- $Id: //core-platform/dev/sql/ccm-core/default/workflow/table-cw_tasks.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table cw_tasks (
  task_id       	 integer
                   	 constraint task_pk primary key,
  parent_task_id         integer
                         constraint task_parent_task_id 
			 references cw_tasks(task_id)
                         on delete cascade,
  label            	 varchar(200)
                   	 constraint task_label_nn not null,
  description      	 varchar(4000),
  is_active        	 char(1) default '0'
                   	 constraint task_is_active_ck
                   	 check (is_active in ('0', '1')),
  task_state             varchar(16)
                         constraint task_state_ck
                         check (task_state in 
                                 ('disabled', 'enabled', 'finished','deleted'))
);
