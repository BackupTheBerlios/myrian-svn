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
-- $Id: //core-platform/dev/sql/default/messaging/table-message_parts.sql#4 $
-- $DateTime: 2003/08/15 13:46:34 $


create table message_parts (
    part_id     integer
                constraint message_parts_part_id_pk 
                    primary key,
    message_id  integer
                constraint message_parts_message_id_fk 
                     references messages(message_id),
    type        varchar(50)
                constraint message_parts_type_nn not null,
    name        varchar(100)
                constraint message_parts_name_nn 
                    not null,
    description varchar(500),
    disposition varchar(50) default 'attachment',
    headers     varchar(4000),
    content     blob
);
