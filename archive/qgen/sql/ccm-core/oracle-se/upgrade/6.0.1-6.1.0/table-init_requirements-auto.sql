create table init_requirements (
    required_init VARCHAR(200) not null,
        -- referential constraint for required_init deferred due to circular dependencies
    init VARCHAR(200) not null,
        -- referential constraint for init deferred due to circular dependencies
    constraint init_requ_ini_req_init_p_qiqj1
      primary key(init, required_init)
);
