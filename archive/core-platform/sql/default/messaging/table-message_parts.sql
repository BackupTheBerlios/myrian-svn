--
-- The message_parts table stores the content (single or multipart) of
-- each message.  When a message is deleted all of its parts are
-- deleted at the same time.  Parts cannot be reused or shared between
-- multiple messages.
--

create table message_parts (
    part_id     integer
                constraint message_parts_part_id_pk 
                    primary key,
    message_id  integer
                constraint message_parts_message_id_fk 
                     references messages(message_id) on delete cascade,
    type        varchar(50)
                constraint message_parts_type_nn not null,
    name        varchar(100)
                constraint message_parts_name_nn 
                    not null,
    description varchar(500),
    disposition varchar(50) default 'attachment',
    headers     varchar(4000),
    content     blob
);
