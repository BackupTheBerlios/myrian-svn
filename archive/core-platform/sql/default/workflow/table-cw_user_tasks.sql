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
