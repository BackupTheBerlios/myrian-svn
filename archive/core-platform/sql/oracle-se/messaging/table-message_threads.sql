create table message_threads (
    thread_id              integer
                           constraint msg_threads_pk
                           primary key
                           constraint msg_threads_thread_id_fk
                           references acs_objects,
    root_id                integer
                           constraint msg_threads_root_id_fk
                           references messages
                           constraint msg_threads_root_id_un
                           unique
                           constraint msg_threads_root_id_nn
                           not null,
    sender                 integer
                           constraint msg_threads_sender_fk  
                           references parties (party_id),
    last_update            date
                           constraint msg_threads_last_update_nn
                           not null,
    num_replies            integer
                           default 0
                           constraint msg_threads_num_repls_nn
                           not null
);
