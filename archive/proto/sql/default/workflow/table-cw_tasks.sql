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
