create table pl_municipalities (
    municipality_id             integer constraint pl_mun_municipality_id_fk
                                references places (place_id)
                                constraint pl_municipalities_pk
                                primary key
);
