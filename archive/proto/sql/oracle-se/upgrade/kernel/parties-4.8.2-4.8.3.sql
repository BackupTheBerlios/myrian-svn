alter table parties modify party_id null;
alter table parties modify party_id constraint parties_party_id_nn not null;

alter table users modify user_id null;
alter table users modify user_id constraint users_user_id_nn not null;

alter table users modify name_id null;
alter table users modify name_id constraint users_person_name_id_nn not null;

alter table groups modify group_id null;
alter table groups modify group_id constraint groups_group_id_nn not null;

alter table group_member_map modify group_id null;
alter table group_member_map modify group_id constraint gmm_group_id_nn not null;

alter table group_member_map modify member_id null;
alter table group_member_map modify member_id constraint gmm_member_id_nn not null;

alter table group_subgroup_map modify group_id null;
alter table group_subgroup_map modify group_id constraint gsm_group_id_nn not null;

alter table group_subgroup_map modify subgroup_id null;
alter table group_subgroup_map modify subgroup_id constraint gsm_subgroup_id_nn not null;

alter table group_subgroup_trans_index modify group_id null;
alter table group_subgroup_trans_index modify group_id constraint gsti_group_id_nn not null;

alter table group_subgroup_trans_index modify subgroup_id null;
alter table group_subgroup_trans_index modify subgroup_id constraint gsti_subgroup_id_nn not null;

alter table group_member_trans_index modify group_id null;
alter table group_member_trans_index modify group_id constraint gmti_group_id_nn not null;

alter table group_member_trans_index modify subgroup_id null;
alter table group_member_trans_index modify subgroup_id constraint gmti_subgroup_id_nn not null;




