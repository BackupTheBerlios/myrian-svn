--
-- add the new table for message threads
--

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
    last_update            date
                           constraint msg_threads_last_update_nn
                           not null
);

--
-- Now we need to do some data migration...
-- TODO
--

declare
  v_thread_id integer;
  v_update    date;

  cursor c_root_msgs 
  is
  select m.message_id, 
         m.subject
  from messages m, acs_objects o
  where o.object_id = m.message_id
  and   m.root_id is null
  and   o.object_type = 'com.arsdigita.messaging.ThreadedMessage';

begin

  for root_msg in c_root_msgs loop
    select acs_object_id_seq.nextval into v_thread_id from dual;

    insert into acs_objects
    (object_id, object_type, display_name, default_domain_class)
    values
    (v_thread_id, 'com.arsdigita.messaging.Thread',
     root_msg.subject, 'com.arsdigita.messaging.MessageThread');

    select max(m2.sent_date) into v_update
    from messages m2
    where m2.root_id = root_msg.message_id
       or m2.message_id = root_msg.message_id;

    insert into message_threads
    (thread_id, root_id, last_update)
    values
    (v_thread_id, root_msg.message_id, v_update);

  end loop;

end;  
/
show errors;

@@ message-thread-add-subject-sender.sql
@@ message-thread-add-num-replies.sql
