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
-- $Id: //core-platform/dev/sql/oracle-se/messaging/messaging-drop.sql#4 $
-- $DateTime: 2003/08/15 13:46:34 $


--
-- packages/messaging/sql/messaging-drop.sql
--
-- @author David Dao <ddao@arsdigita.com>
-- @creation-date 2001-05-24
--

drop index messages_object_idx;
drop index messages_thread_idx;
drop index messages_sender_idx;
drop index messages_reply_to_idx;

drop table message_threads;
drop table message_parts;
drop table messages;
