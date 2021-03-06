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
-- $Id: //core-platform/proto/sql/postgres/upgrade/5.2.0-5.2.1/index-nt_requests.sql#2 $
-- $DateTime: 2003/08/04 16:15:53 $

create index nt_requests_digest_id_idx on nt_requests(digest_id);
create index nt_requests_message_id_idx on nt_requests(message_id);
create index nt_requests_party_to_idx on nt_requests(party_to);
