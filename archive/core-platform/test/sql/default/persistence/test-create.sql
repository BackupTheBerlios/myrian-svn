create table icles (
       icle_id integer not null primary key,
       name varchar(200)
);

create table tests (
       test_id integer not null primary key,
       name varchar(200),
       parent_id integer references tests(test_id),
       optional_self_id integer references tests(test_id),
       optional_id integer references icles(icle_id),
       required_id integer not null references icles(icle_id)
);

create table collection_self (
       test_id integer not null references tests(test_id),
       element_id integer not null references tests(test_id),
       primary key (test_id, element_id)
);

create table collection (
       test_id integer not null references tests(test_id),
       element_id integer not null references icles(icle_id),
       primary key (test_id, element_id)
);

create table components (
       component_id integer not null primary key,
       test_id integer not null references tests(test_id),
       name varchar(200)
);
