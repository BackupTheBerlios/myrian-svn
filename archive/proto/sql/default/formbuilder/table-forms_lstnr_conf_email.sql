create table forms_lstnr_conf_email (
    listener_id integer
        constraint forms_lstnr_conf_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_conf_email_pk primary key,
    sender varchar(120),
    subject varchar(120),
    -- XXX may need to make this a blob
    body varchar(4000)
);
