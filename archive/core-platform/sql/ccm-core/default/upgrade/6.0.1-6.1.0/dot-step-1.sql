alter table persistence_dynamic_ot rename to persistence_dynamic_ot_backup;
alter table persistence_dynamic_assoc rename to persistence_dynamic_assoc_backup;

create table persistence_dynamic_ot (
    pdl_id                 integer,
    pdl_file               varchar(4000),
    dynamic_object_type    varchar(700)
);

create table persistence_dynamic_assoc (
    pdl_id                 integer,
    pdl_file               varchar(4000),
    model_name             varchar(200),
    object_type_one        varchar(500),
    property_one           varchar(100),
    object_type_two        varchar(500),
    property_two           varchar(100)
);
