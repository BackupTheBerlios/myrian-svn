create table forms_dataquery (
    query_id integer 
        constraint forms_dq_query_id_pk primary key
        constraint forms_dq_query_id_fk 
        references acs_objects (object_id) on delete cascade,
    type_id integer
        constraint forms_dq_query_type_id_fk references
        bebop_object_type on delete cascade,
    description varchar(200),
    name varchar(60),
    constraint forms_dataquery_un unique (type_id, name)
);
