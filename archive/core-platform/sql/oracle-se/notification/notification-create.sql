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
-- Notification service
--
-- This file defines the data model for the ACS notification service.  
--
-- @author ron@arsdigita.com
-- @author ddao@arsdigita.com
-- @version $Id: //core-platform/dev/sql/oracle-se/notification/notification-create.sql#3 $

-- Digest support

create table nt_digests (
    digest_id         integer
                      constraint nt_digest_pk
                          primary key
                      constraint nt_digest_fk
                          references acs_objects(object_id),
    party_from        integer
                      constraint nt_digest_party_from_fk
                          references parties(party_id),
    subject           varchar(250)
                      constraint nt_digest_subject_nn 
                          not null,
    header            varchar(4000)
                      constraint nt_digest_header_nn
                          not null,
    separator         varchar(100)
                      constraint nt_digest_separator_nn
                          not null,
    signature         varchar(4000)
                      constraint nt_digest_signature_nn
                          not null,
    frequency         integer
                      default 15
                      constraint nt_digest_frequence_nn
                          not null,
    next_run          date
                      constraint nt_digest_next_run_nn
                          not null
);

-- foreign key index
create index nt_digests_party_from_idx on nt_digests(party_from);

-- Table for storing notification requests

create table nt_requests (
    request_id        integer
                      constraint nt_requests_pk
                          primary key
                      constraint nt_requests_fk
                          references acs_objects(object_id),
    digest_id         integer
                      constraint nt_requests_digest_fk
                          references nt_digests(digest_id),
    party_to          integer
                      constraint nt_requests_party_to_fk
                          references parties(party_id),
    message_id        integer                         
                      constraint nt_requests_message_fk
                          references messages(message_id),
    header            varchar(4000),
    signature         varchar(4000),
    expand_group      char(1)
                      default '1'
                      constraint nt_requests_expand_ck
                          check (expand_group in ('0','1')),
    request_date      date
                      default sysdate,
    fulfill_date      date,
    status            varchar(20)
                      default 'pending'
                      constraint nt_requests_status_ck
                          check (status in 
                              ('pending',
                               'queued',
                               'sent',
                               'failed_partial',
                               'failed',
                               'cancelled')),
    max_retries       integer
                      default 3
                      constraint nt_requests_retries_nn
                          not null,
    expunge_p         char(1)
                      default '1'
                      constraint nt_requests_expunge_ck
                          check (expunge_p in ('0','1')),
    expunge_msg_p     char(1)
                      default '1'
                      constraint nt_requests_expunge_msg_ck
                         check (expunge_msg_p in ('0','1'))
);

-- foreign key indexes
create index nt_requests_digest_id_idx on nt_requests(digest_id);
create index nt_requests_message_id_idx on nt_requests(message_id);
create index nt_requests_party_to_idx on nt_requests(party_to);


-- Explanation of request status:
--
-- pending             request is only in the request table, not queued
-- queued              request is in the queue, failed 0 or more times
-- sent                request has been processed successfully
-- failed              request has failed max_retries times without sucess
-- failed_partial      some components of the request of have failed,
--                     others have succeeded (only applies when
--                     recipient is a group and expand_p = 1) 
-- cancelled           request was cancelled

-- Outbound message queue

create table nt_queue (
    request_id        integer
                      constraint nt_queue_request_fk 
                          references nt_requests(request_id)
                      on delete cascade,
    party_to          integer
                      constraint nt_queue_party_to_fk
                          references parties(party_id)
                      on delete cascade,
    retry_count       integer
                      default 0,
    success_p         char(1)
                      default '1'
                      constraint nt_queue_success_ck
                          check (success_p in ('0','1')),
    constraint nt_queue_composite_pk primary key (request_id, party_to)
);
