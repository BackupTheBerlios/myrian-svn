create table cw_processes (
  process_id             integer 
                         constraint processes_pk
                         primary key
               		 constraint process_task_id_fk 
               		 references cw_tasks,
  process_def_id         integer
               		 constraint process_process_def_id_fk 
               		 references cw_tasks on delete cascade,
  process_state          varchar(16)
                         constraint process_state_ck
                         check (process_state in ('stopped', 'started','deleted','init')),
  object_id              integer
                         constraint processes_object_fk
                         references acs_objects on delete cascade
);
