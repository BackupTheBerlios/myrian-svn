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
