create table vcx_generic_operations (
    id INTEGER not null
        constraint vcx_gener_operation_id_p_dfiws
          primary key,
        -- referential constraint for id deferred due to circular dependencies
    value VARCHAR(4000)
);
