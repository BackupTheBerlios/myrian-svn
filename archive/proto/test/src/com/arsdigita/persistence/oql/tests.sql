create table tests (
    test_id INTEGER not null
        constraint tests_test_id_p_NbHCI
          primary key,
    name VARCHAR(200),
    optional_id INTEGER
        constraint tests_optional_id_f_9JITZ
          references icles(icle_id),
    optional_self_id INTEGER
        constraint tests_optional_self_id_f_fARAW
          references tests(test_id),
    parent_id INTEGER
        constraint tests_parent_id_f_SWQ6G
          references tests(test_id) on delete cascade,
    required_id INTEGER not null
        constraint tests_required_id_f_DH0cL
          references icles(icle_id)
);
