create table bebop_components (
    component_id	    integer
                        constraint bebop_components_id_fk
                        references acs_objects (object_id)
			            constraint bebop_components_pk
			            primary key,
    admin_name          varchar(100),
    description         varchar(4000),
    attribute_string	varchar(4000),
    active_p            char(1)
);
