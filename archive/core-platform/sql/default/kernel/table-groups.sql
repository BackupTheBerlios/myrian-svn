----------------------------
-- GROUP TYPES AND GROUPS --
----------------------------

create table groups (
    group_id    integer
                constraint groups_group_id_nn
                not null
                constraint groups_group_id_fk
                references parties (party_id)
                constraint groups_pk primary key,
	name		varchar(200) not null
);
