create table collection_self (
    element_id INTEGER not null
        constraint collecti_sel_elemen_id_f_rmprl
          references tests(test_id),
    test_id INTEGER not null
        constraint collectio_self_test_id_f_ckdca
          references tests(test_id),
    constraint coll_sel_ele_id_tes_id_p_7m18z
      primary key(test_id, element_id)
)