create table group_member_map (
        id              integer not null
                        constraint gmm_membership_id_pk
                        primary key,
	group_id            integer
                        constraint gmm_group_id_nn
                        not null
                        constraint gmm_group_id_fk
                       	references groups(group_id)
                        on delete cascade,
	member_id           integer 
                        constraint gmm_member_id_nn
                        not null
                       	constraint gmm_member_id_fk
                        references users(user_id),
	constraint gmm_group_member_un unique(group_id, member_id)
);
