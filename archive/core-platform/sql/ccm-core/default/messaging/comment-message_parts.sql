--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/messaging/comment-message_parts.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

comment on table message_parts is '
    A table to store the content parts of a message.  A message is
    determined to be "multipart/mixed" by virtue of having more than
    one part.
';
comment on column message_parts.part_id is '
    Primary key for the message_parts table, doubles as the Content-ID
    when a full MIME Part is created from a row of this table.
';
comment on column message_parts.message_id is '
    Pointer to the message that contains this part.
';
comment on column message_parts.type is '
    MIME type of this part.
';
comment on column message_parts.name is '
    Name of the part.
';
comment on column message_parts.description is '
    Description of the part.
';
comment on column message_parts.disposition is '
    Disposition of the part.  The disposition describes how the part
    should be presented to the user (see RFC 2183).
';
comment on column message_parts.headers is '
    Other MIME headers, stored as multiple lines in a single text
    block.  They are all optional.
';
comment on column message_parts.content is '
    Content of the part.  Proper handling of the content is determined
    by its MIME type.
';
