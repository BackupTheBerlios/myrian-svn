alter table group_member_map drop constraint gmm_group_id_fk;
alter table group_member_map add constraint gmm_group_id_fk foreign key (group_id) references groups on delete cascade;
