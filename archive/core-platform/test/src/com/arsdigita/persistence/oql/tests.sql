create table tests (
    test_id INTEGER not null
        primary key,
    name VARCHAR(200),
    optional_id INTEGER
        references icles(icle_id),
    optional_self_id INTEGER
        references tests(test_id),
    parent_id INTEGER
        references tests(test_id),
    required_id INTEGER not null
        references icles(icle_id)
);
