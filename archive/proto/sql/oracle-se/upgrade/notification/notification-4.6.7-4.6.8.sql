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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/notification/notification-4.6.7-4.6.8.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


-- Upgrades the data model from version 4.6.7 to 4.6.8
--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
--
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/notification/notification-4.6.7-4.6.8.sql#1 $

create index nt_digests_party_from_idx on nt_digests(party_from);
create index nt_requests_digest_id_idx on nt_requests(digest_id);
create index nt_requests_message_id_idx on nt_requests(message_id);
create index nt_requests_party_to_idx on nt_requests(party_to);
