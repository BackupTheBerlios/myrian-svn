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
