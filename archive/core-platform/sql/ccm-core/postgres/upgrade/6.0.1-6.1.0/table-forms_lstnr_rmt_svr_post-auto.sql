create table forms_lstnr_rmt_svr_post (
    listener_id INTEGER not null
        constraint form_lst_rmt_svr_pos_l_p_d2ck9
          primary key,
        -- referential constraint for listener_id deferred due to circular dependencies
    remove_url VARCHAR(700) not null
);
