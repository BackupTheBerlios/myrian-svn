-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
\i ../../../default/kernel/table-acs_privilege_hierarchy.sql
\i ../../../default/kernel/index-acs_privilege_hierarchy.sql
\i ../../../default/kernel/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
\i ../../../default/kernel/table-dnm_privileges.sql
\i ../../../default/kernel/comment-dnm_privileges.sql
\i ../../../default/kernel/table-dnm_privilege_col_map.sql
\i ../../../default/kernel/comment-dnm_privilege_col_map.sql
\i ../../../default/kernel/table-dnm_privilege_hierarchy_map.sql
\i ../../../default/kernel/table-dnm_privilege_hierarchy.sql
\i ../../../default/kernel/comment-dnm_privilege_hierarchy.sql
\i ../../../default/kernel/table-dnm_permissions.sql
\i ../../../default/kernel/comment-dnm_permissions.sql
\i ../../../default/kernel/index-dnm_permissions.sql

\i ../../../postgres/kernel/package-dnm_privileges.sql
\i ../../../default/upgrade/6.0.0-6.1.0/insert-acs_privilege_hierarchy.sql
\i upgrade-dnm_privileges.sql

-- Object context denormalization
\i ../../../default/kernel/table-dnm_object_1_granted_context.sql
\i ../../../default/kernel/table-dnm_object_grants.sql
\i ../../../default/kernel/table-dnm_granted_context.sql
\i ../../../default/kernel/index-dnm_object_1_granted_context.sql
\i ../../../default/kernel/index-dnm_granted_context.sql
\i ../../../postgres/kernel/index-dnm_granted_context.sql
\i ../../../postgres/kernel/table-dnm_ungranted_context.sql
\i ../../../postgres/kernel/index-dnm_ungranted_context.sql

\i ../../../default/kernel/insert-dnm_context.sql
\i ../../../postgres/kernel/package-dnm_context.sql
\i upgrade-dnm_context.sql

-- Party denormalization
\i ../../../default/kernel/table-dnm_group_membership.sql
\i ../../../default/kernel/index-dnm_group_membership.sql
\i ../../../default/kernel/table-dnm_party_grants.sql
\i ../../../postgres/kernel/package-dnm_parties.sql
\i ../../../default/kernel/insert-dnm_group_membership.sql
\i upgrade-dnm_parties.sql

\i ../../../postgres/kernel/triggers-dnm_privileges.sql
\i ../../../postgres/kernel/triggers-dnm_context.sql
\i ../../../postgres/kernel/triggers-dnm_parties.sql

drop trigger object_context_in_tr on object_context;
drop trigger object_context_up_tr on object_context;
drop trigger object_context_del_tr on object_context;
drop trigger acs_objects_context_in_tr on acs_objects;
drop trigger acs_permissions_in_tr on acs_permissions;
drop trigger acs_permissions_up_tr on acs_permissions;
drop trigger acs_permissions_del_tr on acs_permissions;

drop function object_context_in_fn();
drop function object_context_up_fn();
drop function object_context_del_fn();
drop function acs_objects_context_in_fn();
drop function acs_permissions_in_fn();
drop function acs_permissions_up_fn();
drop function acs_permissions_del_fn();

drop function permissions_add_context (integer, integer);
drop function permissions_remove_context (integer, integer);
drop function permissions_add_grant(integer);
drop function permissions_remove_grant(integer);
drop function permissions_rebuild();

drop view all_context_non_leaf_map cascade;
drop view granted_trans_context_index cascade;
drop view granted_trans_context_map cascade;
drop view ungranted_trans_context_index cascade;
drop view ungranted_trans_context_map cascade;
drop view object_context_trans_map cascade;

drop table granted_context_non_leaf_map;
drop table ungranted_context_non_leaf_map;
drop table object_grants;
drop table context_child_counts;
drop table object_context_map;

