create table collection_self (
    test_id INTEGER not null
        constraint collectio_self_test_id_f_ckdca
          references tests(test_id) on delete cascade,
    element_id INTEGER not null
        constraint collecti_sel_elemen_id_f_rmprl
          references tests(test_id) on delete cascade,
    constraint coll_sel_ele_id_tes_id_p_7m18z
      primary key(element_id, test_id)
);
