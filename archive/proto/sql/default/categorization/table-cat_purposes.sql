-- Category purposes are used to define what the various top-level
-- branches (i.e. direct children of the "/" category) are to be used for
create table cat_purposes (
    purpose_id         integer
                       constraint cat_purposes_purpose_id_fk
                       references acs_objects on delete cascade
                       constraint cat_purposes_pk
                       primary key,
    key                varchar(40) not null
                       constraint cat_purposes_key_un unique,
    name               varchar(200) not null,
    description        varchar(4000)
);
