create table vcx_tags (
    id INTEGER not null
        constraint vcx_tags_id_p_sm34b
          primary key,
    txn_id INTEGER,
        -- referential constraint for txn_id deferred due to circular dependencies
    tag VARCHAR(4000),
    tagged_oid VARCHAR(4000)
);
