create table pl_postal_codes (
    postal_code_id              constraint pl_pc_postal_code_id_fk
                                references places (place_id)
                                constraint pl_postal_codes_pk
                                primary key,
    postal_code                 varchar(100)
                                constraint pl_pc_postal_code_nn
                                not null
);
