-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
@@ ../../../default/kernel/table-acs_privilege_hierarchy.sql
@@ ../../../default/kernel/index-acs_privilege_hierarchy.sql
@@ ../../../default/kernel/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
@@ ../../../default/kernel/table-dnm_privileges.sql
@@ ../../../default/kernel/comment-dnm_privileges.sql
@@ ../../../default/kernel/table-dnm_privilege_col_map.sql
@@ ../../../default/kernel/comment-dnm_privilege_col_map.sql
@@ ../../../default/kernel/table-dnm_privilege_hierarchy_map.sql
@@ ../../../default/kernel/table-dnm_privilege_hierarchy.sql
@@ ../../../default/kernel/comment-dnm_privilege_hierarchy.sql
@@ ../../../default/kernel/table-dnm_permissions.sql
@@ ../../../default/kernel/comment-dnm_permissions.sql
@@ ../../../default/kernel/index-dnm_permissions.sql

@@ ../../../oracle-se/kernel/package-dnm_privileges.sql
@@ ../../../default/upgrade/6.0.0-6.1.0/insert-acs_privilege_hierarchy.sql
@@ upgrade-dnm_privileges.sql

-- Object context denormalization
@@ ../../../default/kernel/table-dnm_object_1_granted_context.sql
@@ ../../../default/kernel/table-dnm_object_grants.sql
@@ ../../../default/kernel/table-dnm_granted_context.sql
@@ ../../../default/kernel/index-dnm_object_1_granted_context.sql
@@ ../../../default/kernel/index-dnm_granted_context.sql

@@ ../../../default/kernel/insert-dnm_context.sql
@@ ../../../oracle-se/kernel/package-dnm_context.sql
@@ upgrade-dnm_context.sql

-- Party denormalization
@@ ../../../default/kernel/table-dnm_group_membership.sql
@@ ../../../default/kernel/index-dnm_group_membership.sql
@@ ../../../default/kernel/table-dnm_party_grants.sql
@@ ../../../oracle-se/kernel/package-dnm_parties.sql
@@ ../../../default/kernel/insert-dnm_group_membership.sql
@@ upgrade-dnm_parties.sql

@@ ../../../oracle-se/kernel/triggers-dnm_privileges.sql
@@ ../../../oracle-se/kernel/triggers-dnm_context.sql
@@ ../../../oracle-se/kernel/triggers-dnm_parties.sql

drop package permission_denormalization;

drop view all_context_non_leaf_map;
drop view granted_trans_context_index;
drop view granted_trans_context_map;
drop view ungranted_trans_context_index;
drop view ungranted_trans_context_map;
drop view object_context_trans_map;

drop table granted_context_non_leaf_map;
drop table ungranted_context_non_leaf_map;
drop table object_grants;
drop table context_child_counts;
drop table object_context_map;

