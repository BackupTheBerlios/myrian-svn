create table pl_regions (
    region_id                   integer constraint pl_regions_region_id_fk
                                references places (place_id)
                                constraint pl_regions_pk
                                primary key
);
