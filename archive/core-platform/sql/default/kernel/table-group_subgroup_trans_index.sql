----------------------
-- DENORMALIZATIONS --
----------------------

create table group_subgroup_trans_index (
	group_id	integer 
                constraint gsti_group_id_nn
                not null
			    constraint gsti_group_id_fk
			    references groups(group_id) on delete cascade,
	subgroup_id	integer
                constraint gsti_subgroup_id_nn
                not null
			    constraint gsti_subgroup_id_fk
			    references groups(group_id) on delete cascade,
    n_paths     integer not null,
	constraint gsti_group_party_pk primary key(group_id, subgroup_id),
    -- This prevents circularity in the group-subgroup graph.
    -- If group_id=subgroup_id then n_paths=0.
	constraint gsti_circularity_ck 
                check ( group_id!=subgroup_id or n_paths=0 ),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gsti_n_paths_ck
                check (n_paths>0 or group_id=subgroup_id)
) organization index;
