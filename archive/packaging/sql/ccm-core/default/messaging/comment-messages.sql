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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/messaging/comment-messages.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

comment on table messages is '
    A generic message which may be attached to any object in the system.
';
comment on column messages.message_id is '
    Primary key for messages.
';
comment on column messages.object_id is '
    An optional ACSObject that this message is attached to.  For example, 
    comments might be attached to a ContentItem, or bboard posts might
    be attached to a forum.
';
comment on column messages.rfc_message_id is '
    The RFC 822 Message-ID when a
    message is transported out of the system via email.
';
comment on column messages.in_reply_to is '
    Pointer to a message this message contains a reply to, for threading.
';
comment on column messages.sent_date is '
    The date the message was sent (may be distinct from when it was created
    or published in the system.)
';
comment on column messages.reply_to is '
    Returned e-mail address. This may be different than sender.
';
comment on column messages.sender is '
    The party who sent the message (may be distinct from the person who
    entered the message in the system.)
';
comment on column messages.subject is '
    The subject of the message.
';
comment on column messages.body is '
    Body of the message.
';
comment on column messages.type is '
    MIME type of the body, should be text/plain or text/html.
';
comment on column messages.root_id is '
    Root message for all elements of a thread.  Combined with the sort
    key, this uniquely determines the location of a threaded message.
';
comment on column messages.sort_key is '
    Sort key for generating threaded messages.  Large enough to store
    100 levels of messages with the 3 characters per level.
';
