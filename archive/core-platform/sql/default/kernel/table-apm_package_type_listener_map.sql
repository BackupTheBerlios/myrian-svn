create table apm_package_type_listener_map (
    package_type_id             integer
                                constraint apm_listener_map_pt_id_fk
                                references apm_package_types
                                on delete cascade
                                constraint apm_listener_map_pt_id_nn
                                not null,    
    listener_id                 integer
                                constraint apm_listener_map_list_id_fk
                                references apm_listeners
                                on delete cascade
                                constraint apm_listener_map_list_id_nn
                                not null,
    constraint apm_listener_map_id_class_un unique
                (package_type_id, listener_id) 
);
