create table acs_auditing (
        object_id          integer constraint audited_acs_object_id_fk
                           references acs_objects on delete cascade
                           constraint audited_acs_object_id_pk
                           primary key,
	creation_user      integer constraint audited_creation_user_fk
                           references users on delete set null,
	creation_date      date not null,
	creation_ip        varchar(50),
	last_modified      date not null,
	modifying_user     integer constraint audited_modifying_user_fk
                           references users on delete set null,
	modifying_ip       varchar(50)
);
