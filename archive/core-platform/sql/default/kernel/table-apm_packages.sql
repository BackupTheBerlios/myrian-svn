create table apm_packages (
    package_id                  constraint apm_packages_package_id_fk
                                references acs_objects(object_id)
                                constraint apm_packages_pack_id_pk primary key,
    package_type_id             constraint apm_packages_type_id_fk
                                references apm_package_types(package_type_id)
                                on delete cascade,
    pretty_name                 varchar(300),
    locale_id                   constraint apm_packages_locale_id_fk
                                references g11n_locales (locale_id)
);
