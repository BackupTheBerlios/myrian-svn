create table forms_lstnr_conf_redirect (
    listener_id integer
        constraint forms_lstnr_conf_redirect_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_conf_redirect_pk primary key,
    url varchar(160)
);
