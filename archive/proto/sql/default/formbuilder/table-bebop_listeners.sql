create table bebop_listeners (
       listener_id           integer
                             constraint bebop_listeners_id_fk
                             references acs_objects(object_id)
                             constraint bebop_listeners_id_pk
                             primary key,
       class_name            varchar(150),
       attribute_string      varchar(4000)
);
