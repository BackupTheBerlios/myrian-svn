--------------------
-- OBJECT CONTEXT --
--------------------
create table object_context (
       object_id            integer not null
                            constraint object_context_object_id_fk 
                            references acs_objects (object_id)
                                on delete cascade
                            constraint object_context_pk primary key,
       context_id           integer constraint object_context_context_id_fk
                            references acs_objects(object_id)
                                on delete cascade
);

-- XXX organization index;
