create table search_test_author (
    author_id INTEGER not null
        constraint sear_tes_auth_autho_id_p_TVHqz
          primary key,
        -- referential constraint for author_id deferred due to circular dependencies
    name VARCHAR(100)
);

create table search_test_book (
    book_id INTEGER not null
        constraint searc_tes_book_book_id_p_GJwYX
          primary key,
        -- referential constraint for book_id deferred due to circular dependencies
    title VARCHAR(100)
);

create table search_test_book_chapter (
    chapter_id INTEGER not null
        constraint sear_tes_boo_cha_cha_i_p_qn3vv
          primary key,
        -- referential constraint for chapter_id deferred due to circular dependencies
    chapter_num INTEGER,
    content CLOB
);

alter table search_test_author add 
    constraint sear_tes_auth_autho_id_f_6WtWC foreign key (author_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book add 
    constraint searc_tes_book_book_id_f_e12NA foreign key (book_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book_chapter add 
    constraint sear_tes_boo_cha_cha_i_f_1_nai foreign key (chapter_id)
      references acs_objects(object_id) on delete cascade;
