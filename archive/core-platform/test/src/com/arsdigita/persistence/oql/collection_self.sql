create table collection_self (
    test_id INTEGER not null
        references tests(test_id) on delete cascade,
    element_id INTEGER not null
        references tests(test_id) on delete cascade,
    primary key(test_id, element_id)
);
