-- Experimental: parameterized privileges such as "Create bboard messages"
create table parameterized_privileges (
    base_privilege varchar(100) not null
                   constraint param_priv_base_privilege_fk
                       references acs_privileges(privilege),
    param_key      varchar(100) not null,
    param_name     varchar(100),
    constraint param_priv_un unique (param_key, base_privilege)
);
