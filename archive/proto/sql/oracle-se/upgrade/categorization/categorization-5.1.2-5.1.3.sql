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


comment on table cat_purposes is '
   Used to define what the various top-level branches 
   (i.e. direct children of the "/" category) are to be used for
';

comment on column cat_purposes.key is '
   Unique integer defined via static final ints in the Java domain class
';
comment on column cat_purposes.name is '
   Name is used to display the category purpose in select lists, etc.
';


create table cat_category_purpose_map (
  category_id             integer
                          constraint cat_cat_pur_map_cat_id_fk
                          references cat_categories on delete cascade,
  purpose_id              integer
                          constraint cat_obj_map_purpose_id_fk
                          references acs_objects on delete cascade,
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id)
);

create index cat_cat_pur_map_cat_id_idx on cat_category_purpose_map(category_id);
