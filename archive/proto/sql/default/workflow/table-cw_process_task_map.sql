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
