-- TO DO: add a constraint that prevents circularity in the subgroup graph.
-- This would probably be enforced by trigger.

create table group_subgroup_map (
        id              integer not null
                        constraint gsm_id_pk
                        primary key,
	group_id            integer
                        constraint gsm_group_id_nn
                        not null
                        constraint gsm_group_id_fk
                        references groups(group_id),
	subgroup_id         integer
                        constraint gsm_subgroup_id_nn
                        not null
                        constraint gsm_subgroup_id_fk
                        references groups(group_id),
	constraint gsm_group_party_un unique(group_id, subgroup_id),
	constraint gsm_circularity_ck check (group_id!=subgroup_id)
);
