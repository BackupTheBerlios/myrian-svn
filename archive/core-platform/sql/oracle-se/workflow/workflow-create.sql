--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--


create sequence cw_sequences start with 1;

create table cw_tasks (
  task_id       	 integer
                   	 constraint task_pk primary key,
  parent_task_id         integer
                         constraint task_parent_task_id 
			 references cw_tasks(task_id)
                         on delete cascade,
  label            	 varchar2(200)
                   	 constraint task_label_nn not null,
  description      	 varchar2(4000),
  is_active        	 char(1) default '0'
                   	 constraint task_is_active_ck
                   	 check (is_active in ('0', '1')),
  task_state             varchar2(16)
                         constraint task_state_ck
                         check (task_state in 
                                 ('disabled', 'enabled', 'finished','deleted'))
);

-- foreign key index
create index cw_tasks_parent_task_id_idx on cw_tasks(parent_task_id);

create table cw_user_tasks (
  task_id                integer
			 constraint user_tasks_task_id_pk primary key
                         constraint user_tasks_task_id_fk references cw_tasks
                         on delete cascade,
  is_locked              char(1) default 'f'
                         constraint task_is_locked_ck
                         check (is_locked in ('t', 'f')),
  locking_user_id	 integer,
  --     use constraint when using users table
  --			 constraint user_tasks_locking_user_id_fk references users,
  start_date		 date,
  due_date               date,
  duration_minutes 	 integer ,
  notification_sender_id integer 
);

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



create table cw_task_comments (
  comment_id		  integer
			  constraint task_comments_comment_id_nn not null,
  task_id                 integer
			  constraint task_comments_task_id_nn not null
			  constraint task_comments_task_id_fk 
			  references cw_tasks(task_id) on delete cascade,
  task_comment		  varchar2(4000),
  comment_date		  date default sysdate,
  party_id		  integer,     
  --
  -- allow same comment on many tasks, many comments on same task
  constraint task_comments_pk
  primary key (comment_id, task_id)
);



create table cw_system_tasks (
  task_id                integer
			 constraint system_tasks_task_id_pk primary key
                         constraint system_tasks_task_id_fk references cw_tasks
                                    on delete cascade
);




-- Task listeners need state-awareness and are thus linked to concrete tasks
create table cw_task_listeners (
  task_id  		 integer
               		 constraint task_listen_task_id_nn not null
               		 constraint task_listen_task_id_fk 
               		 references cw_tasks on delete cascade,
  listener_task_id	 integer
               		 constraint listen_task_id_nn not null
                         constraint listen_task_id_fk 
                             references cw_tasks on delete cascade,
  constraint task_listeners_pk
  primary key (task_id, listener_task_id)  
);


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


create table cw_task_group_assignees (
  --     
  -- task_id either references cw_user_tasks.task_id for the assignees
  -- 
  task_id  		 integer
               		 constraint task_group_task_id_nn not null
	                 constraint task_group_task_id_fk 
                             references cw_tasks(task_id) 
                             on delete cascade,
  group_id               integer
               		 constraint group_task_id_nn not null
                         constraint group_task_id_fk 
                            references groups(group_id) on delete cascade,
  constraint task_group_assignees_pk
  primary key (task_id, group_id)  
);


create table cw_processes (
  process_id             integer 
                         constraint processes_pk
                         primary key
               		 constraint process_task_id_fk 
               		 references cw_tasks,
  process_def_id         integer
               		 constraint process_process_def_id_fk 
               		 references cw_tasks on delete cascade,
  process_state          varchar2(16)
                         constraint process_state_ck
                         check (process_state in ('stopped', 'started','deleted','init')),
  object_id              integer
                         constraint processes_object_fk
                         references acs_objects on delete cascade
);

-- foreign key index
create index cw_processes_object_id_idx on cw_processes(object_id);


create table cw_process_task_map (
  process_id             integer
               		 constraint map_process_id_nn not null
               		 constraint map_process_id_fk  
               		 references cw_processes on delete cascade,
  task_id  		 integer
               		 constraint map_task_id_nn not null
               		 constraint map_task_id_fk 
               		 references cw_tasks on delete cascade,
  constraint process_task_map_pk
  primary key (process_id, task_id)  
);


create table cw_process_definitions (
  process_def_id  	 integer
                  	 constraint process_def_pk primary key
                  	 constraint process_def_id_fk 
                  	 references cw_processes
);

-- foreign key index
create index cw_processes_process_def_idx on cw_processes(process_def_id);
