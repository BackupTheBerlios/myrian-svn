create table pl_us_counties (
    county_id                   constraint pl_usc_county_id_fk
                                references places (place_id)
                                constraint pl_us_counties_pk
                                primary key,
    fips_code                   char(6)
                                constraint pl_usc_fips_code_nn
                                not null,
    state_fips_code             constraint pl_usc_state_fips_code_fk
                                references pl_us_states (fips_code)
                                constraint pl_usc_state_fips_code_nn
                                not null
);
