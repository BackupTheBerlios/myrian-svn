create table pl_us_states (
    state_id                    integer constraint pl_uss_state_id_fk
                                references pl_regions (region_id)
                                constraint pl_uss_states_pk
                                primary key,
    fips_code                   char(2)
                                constraint pl_uss_fips_code_un
                                unique
                                constraint pl_uss_fips_code_nn
                                not null,
    usps_abbrev                 char(2)
                                constraint pl_uss_usps_abbrev_un
                                unique
                                constraint pl_uss_usps_abbrev_nn
                                not null
);
