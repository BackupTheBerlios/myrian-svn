create table bebop_object_type (
    type_id integer
        constraint bebop_object_type_type_id_fk references
        acs_objects (object_id) on delete cascade
        constraint bebop_object_type_type_id_pk primary key,
    app_type varchar(20)
        constraint bebop_object_type_app_nn not null,
    class_name varchar(120)
        constraint bebop_object_type_class_nn not null,
    constraint bebop_object_type_un unique(app_type, class_name)
);
