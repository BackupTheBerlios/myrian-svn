--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/oracle-se/messaging/table-messages.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

create table messages ( 
    message_id     integer
                   constraint messages_message_id_fk
                       references acs_objects(object_id)
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
