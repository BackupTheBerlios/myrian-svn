--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/test/sql/default/kernel/url-service-test.sql#3 $
-- $DateTime: 2003/08/15 13:46:34 $


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
