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
