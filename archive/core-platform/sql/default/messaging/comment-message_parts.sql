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
