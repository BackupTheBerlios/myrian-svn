create table g11n_catalogs (
    catalog_id                  integer
                                constraint g11n_catalogs_pk
                                primary key,
    catalog_name                varchar(400)
                                constraint g11n_catalogs_catalog_name_nn
                                not null,
    locale_id                   integer
                                constraint g11n_catalogs_locale_id_fk
                                references g11n_locales (locale_id),
    catalog                     blob,
    last_modified               date
                                constraint g11n_catalogs_last_modified_nn
                                not null
);
