--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/messaging/table-message_threads.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

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
