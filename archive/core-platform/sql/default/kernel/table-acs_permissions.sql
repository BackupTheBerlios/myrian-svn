-----------------
-- PERMISSIONS --
-----------------
create table acs_permissions (
       object_id             not null
                             constraint acs_permissions_on_what_id_fk
                             references acs_objects (object_id)
                                 on delete cascade,
       grantee_id            not null
                             constraint acs_permissions_grantee_id_fk
                             references parties (party_id)
                                 on delete cascade,
       privilege             not null
                             constraint acs_permissions_priv_fk
                             references acs_privileges (privilege)
                                 on delete cascade,
       constraint acs_premissions_pk
       primary key (object_id, grantee_id, privilege),
	   creation_user         constraint acs_perm_creation_user_fk
                             references users on delete set null,
	   creation_date         date default sysdate not null,
	   creation_ip           varchar(50)
);
