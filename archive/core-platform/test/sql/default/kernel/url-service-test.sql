--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/test/sql/default/kernel/url-service-test.sql#4 $
-- $DateTime: 2004/03/30 17:47:27 $


create table t_forums (
    forum_id    integer not null
                primary key
                references acs_objects(object_id),
    package_id  integer not null references apm_packages (package_id),
    name        varchar(30) not null
);

create table t_messages (
    message_id  integer not null
                primary key
                references acs_objects(object_id),
    forum_id    integer not null references t_forums(forum_id),
    subject     varchar(200) not null,
    message     varchar(4000) not null
);
