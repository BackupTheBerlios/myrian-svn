--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/default/messaging/index-messages.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

create index messages_object_idx   on messages (message_id, object_id);
create index messages_reply_to_idx on messages (in_reply_to);
create index messages_sender_idx   on messages (sender);
create index messages_sent_date_idx on messages(sent_date);
create index messages_thread_idx   on messages (root_id, sort_key);
