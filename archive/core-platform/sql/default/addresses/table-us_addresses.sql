create table us_addresses (
    address_id                  constraint us_addresses_address_id_fk
                                references acs_objects (object_id)
                                constraint us_addresses_pk
                                primary key,
    line1                       varchar(200)
                                constraint us_addresses_line1_nn
                                not null,
    line2                       varchar(200),
    line3                       varchar(200),
    line4                       varchar(200),
    city                        varchar(200)
                                constraint us_addresses_city_nn
                                not null,
    state                       char(2)
                                constraint us_addresses_state_nn
                                not null,
    zip                         char(5)
                                constraint us_addresses_zip_nn
                                not null,
    zip_ext                     char(4)
--    city_id                     constraint us_addresses_city_id_fk
--                                references us_cities (city_id)
--                                constraint us_addresses_city_id_nn
--                                not null,
--    state_id                    constraint us_addresses_state_id_fk
--                                references us_states (state_id)
--                                constraint us_addresses_state_id_nn
--                                not null,
--    zip_id                      constraint us_addresses_zip_id_fk
--                                references us_zip_codes (zip_id)
--                                constraint us_addreses_zip_id_nn
--                                not null
);
