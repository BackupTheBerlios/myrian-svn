create table vcx_txns (
    id INTEGER not null
        constraint vcx_txns_id_p_himn5
          primary key,
    modifying_ip VARCHAR(400),
    timestamp DATE not null,
    modifying_user INTEGER
        -- referential constraint for modifying_user deferred due to circular dependencies
);
