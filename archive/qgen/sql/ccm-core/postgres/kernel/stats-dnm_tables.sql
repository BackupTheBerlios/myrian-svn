alter table dnm_granted_context alter pd_object_id  set statistics 1000;
alter table dnm_granted_context alter pd_context_id set statistics 1000;

alter table dnm_group_membership alter pd_group_id set statistics 1000;
alter table dnm_group_membership alter pd_member_id set statistics 1000;

alter table dnm_object_1_granted_context alter pd_object_id set statistics 1000;
alter table dnm_object_1_granted_context alter pd_context_id set statistics 1000;
alter table dnm_object_1_granted_context alter pd_non_effective_context_id set statistics 1000;

alter table dnm_object_grants alter pd_object_id set statistics 1000;

alter table dnm_party_grants alter pd_party_id set statistics 1000;

alter table dnm_permissions alter pd_object_id set statistics 1000;
alter table dnm_permissions alter pd_grantee_id set statistics 1000;

alter table dnm_ungranted_context alter object_id set statistics 1000;
alter table dnm_ungranted_context alter ancestor_id set statistics 1000;
alter table dnm_ungranted_context alter granted_context_id set statistics 1000;
