create table bebop_process_listeners (
	  listener_id		integer
                        constraint bebop_process_listeners_fk
                        references acs_objects (object_id) on delete cascade
                        constraint bebop_process_listeners_pk
                        primary key,
      name              varchar(40),
      description       varchar(120),
      listener_class    varchar(100)
);
