create table vcx_blob_operations (
    id INTEGER not null
        constraint vcx_blob_operations_id_p_85h09
          primary key,
        -- referential constraint for id deferred due to circular dependencies
    value BYTEA
);
