create table group_member_trans_index (
	group_id	integer 
                constraint gmti_group_id_nn
                not null
			    constraint gmti_group_id_fk
			    references groups(group_id) on delete cascade,
	member_id	integer
                constraint gmti_subgroup_id_nn
                not null
			    constraint gmti_subgroup_id_fk
			    references users(user_id) on delete cascade,
    n_paths     integer not null,
	constraint gmti_group_party_pk primary key(group_id, member_id),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gmti_n_paths_ck
                check (n_paths>0)
) organization index;
