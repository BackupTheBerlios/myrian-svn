create table collection (
    test_id INTEGER not null
        references tests(test_id),
    element_id INTEGER not null
        references icles(icle_id),
    primary key(test_id, element_id)
);
