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
