create table vcx_obj_changes (
    id INTEGER not null
        constraint vcx_obj_changes_id_p_hvgb7
          primary key,
    txn_id INTEGER,
        -- referential constraint for txn_id deferred due to circular dependencies
    obj_id VARCHAR(400) not null
);
