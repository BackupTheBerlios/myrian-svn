create table lucene_ids (
    id INTEGER not null
        constraint lucene_ids_id_p_amiqo
          primary key,
    host_id INTEGER not null
        constraint lucene_ids_host_id_u_q0oag
          unique,
        -- referential constraint for host_id deferred due to circular dependencies
    index_id INTEGER not null
        constraint lucene_ids_index_id_u_j8uuw
          unique
);
