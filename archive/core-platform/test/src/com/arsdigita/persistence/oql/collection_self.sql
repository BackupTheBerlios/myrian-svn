create table collection_self (
    test_id INTEGER not null
        references tests(test_id),
    element_id INTEGER not null
        references tests(test_id),
    primary key(test_id, element_id)
);
