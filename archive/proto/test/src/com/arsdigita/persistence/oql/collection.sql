create table collection (
    test_id INTEGER not null
        constraint collection_test_id_f_faeki
          references tests(test_id) on delete cascade,
    element_id INTEGER not null
        constraint collection_element_id_f_4qmqe
          references icles(icle_id) on delete cascade,
    constraint collect_elem_id_tes_id_p_zk_qs
      primary key(element_id, test_id)
);
