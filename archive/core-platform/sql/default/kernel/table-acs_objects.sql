create table acs_objects (
	object_id		        integer not null
				            constraint acs_objects_pk primary key,
	object_type		        varchar(100) not null,
    display_name            varchar(200) not null,
    default_domain_class    varchar(100)
);
