create table apm_package_types (
    package_type_id             integer 
                                constraint apm_package_types_pk primary key,
    package_key                 varchar(100)
                                constraint apm_package_types_key_nn not null
                                constraint apm_package_types_key_un unique,
    pretty_name                 varchar(100)
                                constraint apm_package_types_pretty_n_nn not null
                                constraint apm_package_types_pretty_n_un unique,
    pretty_plural               varchar(100)
                                constraint apm_package_types_pretty_pl_un unique,
    package_uri                 varchar(1500)
                                constraint apm_packages_types_p_uri_nn not null
                                constraint apm_packages_types_p_uri_un unique,
    servlet_package             varchar(100),
    dispatcher_class            varchar(100) 
        default 'com.arsdigita.dispatcher.JSPApplicationDispatcher'
);
