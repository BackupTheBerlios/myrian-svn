create table vcx_operations (
    id INTEGER not null
        constraint vcx_operations_id_p_p2kfb
          primary key,
    change_id INTEGER,
        -- referential constraint for change_id deferred due to circular dependencies
    event_type_id INTEGER not null,
        -- referential constraint for event_type_id deferred due to circular dependencies
    attribute VARCHAR(200) not null,
    subtype INTEGER not null,
    class_id INTEGER not null
        -- referential constraint for class_id deferred due to circular dependencies
);
