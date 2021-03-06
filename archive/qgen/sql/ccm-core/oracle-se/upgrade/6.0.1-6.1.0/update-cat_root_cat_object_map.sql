create table cat_root_cat_object_map_temp (
    id NUMERIC not null
        constraint temp_cat_1
          primary key,
    category_id INTEGER not null,
    object_id INTEGER not null,
    use_context VARCHAR(700),
    constraint temp_cat_2
      unique(object_id, use_context)
);

insert into cat_root_cat_object_map_temp
  (id, category_id, object_id, use_context)
select acs_object_id_seq.nextval, category_id, object_id, null
  from cat_root_cat_object_map;

drop table cat_root_cat_object_map;
alter table cat_root_cat_object_map_temp rename to cat_root_cat_object_map;

alter table cat_root_cat_object_map drop constraint temp_cat_1;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_id_p_qw9kr
      primary key (id);

alter table cat_root_cat_object_map drop constraint temp_cat_2;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_ob_u_gqgrh
      unique (object_id, use_context);

alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ca_f_jqvmd foreign key (category_id)
      references cat_categories(category_id);
alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ob_f_anfmx foreign key (object_id)
      references acs_objects(object_id);

create index cat_roo_cat_obj_map_cat_id_idx on cat_root_cat_object_map(category_id);
