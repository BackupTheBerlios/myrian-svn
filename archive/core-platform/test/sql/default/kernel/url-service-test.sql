
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
