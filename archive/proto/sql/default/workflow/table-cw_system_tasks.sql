create table cw_system_tasks (
  task_id                integer
			 constraint system_tasks_task_id_pk primary key
                         constraint system_tasks_task_id_fk references cw_tasks
                                    on delete cascade
);
