create table components (
    component_id INTEGER not null
        primary key,
    name VARCHAR(200),
    test_id INTEGER
        references tests(test_id)
);
