--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
--
-- packages/messaging/sql/messaging-create.sql
--
-- @author Ron Henderson <ron@arsdigita.com>
-- @author David Dao <ddao@arsdigita.com>
-- @author John Prevost <jmp@arsdigita.com>
--
-- @version $Id: //core-platform/dev/sql/oracle-se/messaging/messaging-create.sql#4 $

--
-- The messages table stores the basic sender and reply-to information
-- for the messages.
--

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
    sent_date      date 
                   default sysdate
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

create index messages_reply_to_idx on messages (in_reply_to);
create index messages_sender_idx   on messages (sender);
create index messages_thread_idx   on messages (root_id, sort_key);
create index messages_object_idx   on messages (message_id, object_id);
create index messages_sent_date_idx on messages(sent_date);

comment on table messages is '
    A generic message which may be attached to any object in the system.
';

comment on column messages.message_id is '
    Primary key for messages.
';


comment on column messages.object_id is '
    An optional ACSObject that this message is attached to.  For example, 
    comments might be attached to a ContentItem, or bboard posts might
    be attached to a forum.
';

comment on column messages.rfc_message_id is '
    The RFC 822 Message-ID when a
    message is transported out of the system via email.
';

comment on column messages.in_reply_to is '
    Pointer to a message this message contains a reply to, for threading.
';

comment on column messages.sent_date is '
    The date the message was sent (may be distinct from when it was created
    or published in the system.)
';

comment on column messages.reply_to is '
    Returned e-mail address. This may be different than sender.
';

comment on column messages.sender is '
    The party who sent the message (may be distinct from the person who
    entered the message in the system.)
';

comment on column messages.subject is '
    The subject of the message.
';

comment on column messages.body is '
    Body of the message.
';

comment on column messages.type is '
    MIME type of the body, should be text/plain or text/html.
';

comment on column messages.root_id is '
    Root message for all elements of a thread.  Combined with the sort
    key, this uniquely determines the location of a threaded message.
';

comment on column messages.sort_key is '
    Sort key for generating threaded messages.  Large enough to store
    100 levels of messages with the 3 characters per level.
';

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

create index message_parts_message_id_idx on message_parts(message_id);

comment on table message_parts is '
    A table to store the content parts of a message.  A message is
    determined to be "multipart/mixed" by virtue of having more than
    one part.
';

comment on column message_parts.part_id is '
    Primary key for the message_parts table, doubles as the Content-ID
    when a full MIME Part is created from a row of this table.
';

comment on column message_parts.message_id is '
    Pointer to the message that contains this part.
';

comment on column message_parts.type is '
    MIME type of this part.
';

comment on column message_parts.name is '
    Name of the part.
';

comment on column message_parts.description is '
    Description of the part.
';

comment on column message_parts.disposition is '
    Disposition of the part.  The disposition describes how the part
    should be presented to the user (see RFC 2183).
';

comment on column message_parts.headers is '
    Other MIME headers, stored as multiple lines in a single text
    block.  They are all optional.
';

comment on column message_parts.content is '
    Content of the part.  Proper handling of the content is determined
    by its MIME type.
';

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



