create table persistence_dynamic_assoc (
    pdl_id                 integer
                           constraint pers_dyn_assoc_pdl_id_fk
                           references acs_objects on delete cascade
                           constraint pers_dyn_assoc_pdl_id_pk
                           primary key,
    pdl_file               text
                           constraint pers_dyn_assoc_pdl_file_nn
                           not null,
    model_name             varchar(200)
                           constraint pers_dyn_assoc_model_nn
                           not null,
    object_type_one        varchar(500)
                           constraint pers_dyn_assoc_object1_nn
                           not null,
    property_one           varchar(100)
                           constraint pers_dyn_assoc_prop1_nn
                           not null,
    object_type_two        varchar(500)
                           constraint pers_dyn_assoc_object2_nn
                           not null,
    property_two           varchar(100)
                           constraint pers_dyn_assoc_prop2_nn
                           not null,
    constraint pers_dyn_assoc_un
    unique (model_name, object_type_one, property_one, object_type_two,
            property_two)
);
