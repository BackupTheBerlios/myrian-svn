create index messages_object_idx   on messages (message_id, object_id);
create index messages_reply_to_idx on messages (in_reply_to);
create index messages_sender_idx   on messages (sender);
create index messages_sent_date_idx on messages(sent_date);
create index messages_thread_idx   on messages (root_id, sort_key);
