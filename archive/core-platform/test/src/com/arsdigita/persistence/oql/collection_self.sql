create table collection_self (
    test_id INTEGER not null
        constraint collectio_self_test_id_f_NvZYL
          references tests(test_id) on delete cascade,
    element_id INTEGER not null
        constraint collecti_sel_elemen_id_f_C8Arl
          references tests(test_id) on delete cascade,
    constraint coll_sel_tes_id_ele_id_p_YAP30
      primary key(element_id, test_id)
);
