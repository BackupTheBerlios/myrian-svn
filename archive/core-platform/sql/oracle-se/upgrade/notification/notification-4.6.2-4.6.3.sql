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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/notification/notification-4.6.2-4.6.3.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


--
-- Constraint names changed for relase 4.6.3, datamodel did not change,
-- so only run the update script if you are a purist.
--

alter table nt_queue drop constraint nt_queue_request_fk;
alter table nt_requests drop constraint nt_request_pk;
alter table nt_requests add (constraint nt_requests_pk primary key (request_id));
alter table nt_queue add constraint nt_queue_request_fk foreign key (request_id) 
	references nt_requests(request_id);

alter table nt_requests drop constraint nt_request_fk;
alter table nt_requests add constraint nt_requests_fk foreign key (request_id) 
	references acs_objects(object_id);

alter table nt_requests drop constraint nt_queue_digest_fk;
alter table nt_requests add  constraint nt_requests_digest_fk foreign key (digest_id) 
	references nt_digests(digest_id);

alter table nt_requests drop constraint nt_request_party_to_fk;
alter table nt_requests add  constraint nt_requests_party_to_fk foreign key (party_to) 
	references parties(party_id);

alter table nt_requests drop constraint nt_request_message_fk;
alter table nt_requests add  constraint nt_requests_message_fk foreign key (message_id) 
	references messages(message_id);

alter table nt_requests drop constraint nt_request_expand_ck;
alter table nt_requests add  constraint nt_requests_expand_ck 
	check (expand_group in ('0','1'));

alter table nt_requests drop constraint nt_request_status_ck;
alter table nt_requests add  constraint nt_requests_status_ck check (status in 
                              ('pending',
                               'queued',
                               'sent',
                               'failed_partial',
                               'failed',
                               'cancelled'));

alter table nt_requests drop constraint nt_request_retries_nn;
alter table nt_requests modify  max_retries constraint nt_requests_retries_nn not null;

alter table nt_requests drop constraint nt_request_expunge_ck;
alter table nt_requests add  constraint nt_requests_expunge_ck 
	check (expunge_p in ('0','1'));

alter table nt_requests drop constraint nt_request_expunge_msg_chk;
alter table nt_requests add  constraint nt_requests_expunge_msg_ck 
	check (expunge_msg_p in ('0','1'));





