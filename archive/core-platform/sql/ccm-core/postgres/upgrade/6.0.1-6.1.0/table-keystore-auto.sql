create table keystore (
    id INTEGER not null
        constraint keystore_id_p_zq0fx
          primary key,
    owner VARCHAR(100) not null
        constraint keystore_owner_u_pbkhc
          unique,
    store BYTEA not null
);
