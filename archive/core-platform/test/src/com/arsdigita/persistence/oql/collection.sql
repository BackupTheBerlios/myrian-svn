create table collection (
    element_id INTEGER not null
        constraint collection_element_id_f_P1x1p
          references icles(icle_id),
    test_id INTEGER not null
        constraint collection_test_id_f_fLpVi
          references tests(test_id),
    constraint collect_tes_id_elem_id_p_XGZN5
      primary key(test_id, element_id)
);
