create table object_container_map (
    object_id               integer not null
                            constraint aocm_object_id_fk
                            references acs_objects (object_id) 
                                on delete cascade
                            constraint aocm_object_id_pk primary key,
    container_id            integer not null
                            constraint aocm_container_id_fk
                            references acs_objects (object_id)
                                on delete cascade
);

-- XXX organization index;
