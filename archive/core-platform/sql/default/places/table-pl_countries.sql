create table pl_countries (
    country_id                  integer constraint pl_countries_country_id_fk
                                references places (place_id)
                                constraint pl_countries_pk
                                primary key,
    iso                         char(2)
                                constraint pl_countries_iso_un
                                unique
                                constraint pl_countries_iso_nn
                                not null
);
