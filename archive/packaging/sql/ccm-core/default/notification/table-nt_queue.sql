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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/notification/table-nt_queue.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

-- Explanation of request status:
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
