alter table message_threads add (
    num_replies            integer
                           default 0
                           constraint msg_threads_num_repls_nn
                           not null
);

update message_threads mt
set mt.num_replies = (select count(*) from messages m
                      where m.root_id = mt.root_id);

commit;
