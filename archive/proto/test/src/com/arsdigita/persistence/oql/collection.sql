create table collection (
    test_id INTEGER not null
        constraint collection_test_id_f_fLpVi
          references tests(test_id) on delete cascade,
    element_id INTEGER not null
        constraint collection_element_id_f_P1x1p
          references icles(icle_id) on delete cascade,
    constraint collect_tes_id_elem_id_p_XGZN5
      primary key(element_id, test_id)
);
