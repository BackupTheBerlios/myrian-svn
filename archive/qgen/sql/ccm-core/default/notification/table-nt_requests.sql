--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/notification/table-nt_requests.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


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
                          references messages(message_id)
                          on delete cascade,
    header            varchar(4000),
    signature         varchar(4000),
    expand_group      char(1)
                      default '1'
                      constraint nt_requests_expand_ck
                          check (expand_group in ('0','1')),
    request_date      timestamptz
                      default current_timestamp,
    fulfill_date      timestamptz,
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
