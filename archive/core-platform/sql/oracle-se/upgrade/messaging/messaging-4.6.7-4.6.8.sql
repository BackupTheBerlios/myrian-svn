--
-- Added support for threaded messages
--
-- @author ron@arsdigita.com
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/messaging/messaging-4.6.7-4.6.8.sql#2 $

alter table messages add (
    object_id   integer
                constraint messages_object_id_fk
                    references acs_objects(object_id) on delete cascade,
    root_id     integer
                constraint messages_root_id_fk
                    references messages(message_id) on delete cascade,
    sort_key    varchar2(300)
);

create index messages_thread_idx on messages (root_id, sort_key);
create index messages_object_idx on messages (message_id, object_id);
