create table tests (
    test_id INTEGER not null
        constraint tests_test_id_p_cq728
          primary key,
    name VARCHAR(200),
    optional_self_id INTEGER
        constraint tests_optional_self_id_f_5060l
          references tests(test_id),
    optional_id INTEGER
        constraint tests_optional_id_f_n9xio
          references icles(icle_id),
    required_id INTEGER not null
        constraint tests_required_id_f_swp2a
          references icles(icle_id),
    parent_id INTEGER
        constraint tests_parent_id_f_hlfvv
          references tests(test_id)
)