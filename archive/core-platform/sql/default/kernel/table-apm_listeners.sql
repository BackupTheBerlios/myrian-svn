create table apm_listeners (
    listener_id                 integer
                                constraint apm_listeners_pk
                                primary key,
    listener_class              varchar(100)
                                constraint apm_listeners_class_nn
                                not null
                                constraint apm_listeners_class_un
                                unique
);
