alter table group_member_map drop column id;
alter table group_member_map add
constraint group_member_map_pk primary key(group_id, member_id);

alter table group_subgroup_map drop column id;
alter table group_subgroup_map add
constraint group_subgroup_map_pk primary key(group_id, member_id);
