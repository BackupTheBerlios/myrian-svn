create table forms_lstnr_simple_email (
    listener_id integer
        constraint forms_lstnr_simple_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_simple_email_pk primary key,
    recipient varchar(120),
    subject varchar(120)
);
