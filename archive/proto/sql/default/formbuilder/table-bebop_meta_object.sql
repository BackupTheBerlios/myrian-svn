create table bebop_meta_object (
    object_id integer
        constraint bebop_meta_obj_object_id_fk references
        acs_objects on delete cascade
        constraint bebop_meta_obj_object_id_pk primary key,
    type_id integer
        constraint bebop_meta_object_type_id_nn not null
        constraint bebop_meta_object_type_id_fk references
        bebop_object_type on delete cascade,
    pretty_name varchar(50),
    pretty_plural varchar(50),
    class_name varchar(200),
    props_form varchar(200),
    constraint bebop_meta_obj_un unique (type_id, class_name)
);
