create table collection (
    element_id INTEGER not null
        constraint collection_element_id_f_4qmqe
          references icles(icle_id),
    test_id INTEGER not null
        constraint collection_test_id_f_faeki
          references tests(test_id),
    constraint collect_elem_id_tes_id_p_zk_qs
      primary key(test_id, element_id)
)
