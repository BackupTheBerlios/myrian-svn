create table components (
    component_id INTEGER not null
        constraint component_component_id_p_6gckk
          primary key,
    name VARCHAR(200),
    test_id INTEGER not null
        constraint components_test_id_f_9042c
          references tests(test_id)
)