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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/messaging/index-messages.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

create index messages_object_idx   on messages (message_id, object_id);
create index messages_reply_to_idx on messages (in_reply_to);
create index messages_sender_idx   on messages (sender);
create index messages_sent_date_idx on messages(sent_date);
create index messages_thread_idx   on messages (root_id, sort_key);
