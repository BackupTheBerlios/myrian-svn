-----------------
-- GROUP ROLES --
-----------------

create table roles (
        role_id           integer 
                          constraint roles_role_id_pk primary key,
        group_id          integer 
                          constraint group_roles_group_id_nn not null
                          constraint group_roles_group_id_fk references groups(group_id)
                          on delete cascade,
        name              varchar(200) 
                          constraint roles_name_nn not null,
        description       varchar(4000),
        implicit_group_id integer 
                          constraint group_roles_impl_group_id_nn not null
                          constraint group_roles_impl_group_id_fk references groups(group_id)
                          on delete cascade,
        constraint roles_different_groups check(implicit_group_id <> group_id),
        constraint roles_group_id_name_un unique(group_id, name)
);
