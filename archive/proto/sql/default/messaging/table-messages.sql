create table messages ( 
    message_id     integer
                   constraint messages_message_id_fk
                       references acs_objects(object_id) on delete cascade
                   constraint messages_message_id_pk
                       primary key,
    object_id      integer
                   constraint messages_object_id_fk
                       references acs_objects(object_id) on delete cascade,
    reply_to       varchar(250),
    sender         integer
                   constraint messages_sender_fk  
                       references parties (party_id),
    subject        varchar(250)
                   constraint messages_subject_nn not null,
    body           varchar(4000)
                   constraint messages_body_nn not null,
    type           varchar(50)
                   constraint messages_type_nn not null,
    sent_date      timestamp 
                   default current_timestamp
                   constraint messages_sent_date_nn not null,
    in_reply_to    integer
                   constraint messages_reply_to_fk
                       references messages(message_id) on delete set null,
    rfc_message_id varchar(250),
    root_id        integer
                   constraint messages_root_id_fk
                       references messages(message_id) on delete cascade,
    sort_key       varchar(300)
);
