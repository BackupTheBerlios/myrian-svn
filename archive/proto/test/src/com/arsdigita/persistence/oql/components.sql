create table components (
    component_id INTEGER not null
        constraint component_component_id_p_GrNkv
          primary key,
    name VARCHAR(200),
    test_id INTEGER
        constraint components_test_id_f_UaeCY
          references tests(test_id) on delete cascade
);
