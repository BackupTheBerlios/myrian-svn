create table cw_process_definitions (
  process_def_id  	 integer
                  	 constraint process_def_pk primary key
                  	 constraint process_def_id_fk 
                  	 references cw_processes
);
