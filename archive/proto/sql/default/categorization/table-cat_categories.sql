create table cat_categories (
    category_id        integer
                       constraint cat_categories_fk
                       references acs_objects on delete cascade
                       constraint cat_categories_pk
                       primary key,
    name               varchar(200) not null,
    description        varchar(4000),
    -- this should deafult to the JDBC version of true
    enabled_p          char(1),
    -- if the category is abstract then that means it cannot have
    -- any child objects (but it can have child categories).
    abstract_p          char(1) default '0'
                        constraint cat_categories_abstract_p_ck
                        check(abstract_p in ('0','1')),
    default_ancestors varchar(4000) not null
);
