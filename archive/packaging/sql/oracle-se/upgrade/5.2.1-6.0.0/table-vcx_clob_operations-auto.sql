create table vcx_clob_operations (
    id INTEGER not null
        constraint vcx_clob_operations_id_p_dpcee
          primary key,
        -- referential constraint for id deferred due to circular dependencies
    value CLOB
);
