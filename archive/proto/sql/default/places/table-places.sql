create table places (
    place_id                    integer
                                constraint places_pk
                                primary key,
    place_name                  varchar(200)
                                constraint places_place_name_nn
                                not null,
    latitude                    numeric(9,6),
    longitude                   numeric(9,6)
);
