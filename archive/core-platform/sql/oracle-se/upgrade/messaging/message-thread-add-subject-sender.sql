alter table message_threads add (
    subject                varchar2(250),
    sender                 integer
                           constraint msg_threads_sender_fk  
                           references parties (party_id)
);

update message_threads mt
set mt.subject = (select m.subject from messages m 
                  where m.message_id = mt.root_id),
    mt.sender = (select m.sender from messages m
                 where m.message_id = mt.root_id);

alter table message_threads modify (
    subject constraint msg_threads_subject_nn not null
);

alter table message_threads modify (
    sender constraint msg_sender_nn not null
);
 
