--
-- Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/upgrade/postgres-6.0.1-6.1.0.sql#5 $
-- $DateTime: 2004/03/16 19:04:14 $

\echo Red Hat WAF 6.0.1 -> 6.1.0 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/6.0.1-6.1.0/table-admin_app-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-agentportlets-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-init_requirements-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-inits-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-keystore-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-lucene_ids-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-sitemap_app-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-webapps-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/deferred.sql
\i ../postgres/upgrade/6.0.1-6.1.0/update-host-unique-index.sql
\i ../postgres/upgrade/6.0.1-6.1.0/update-cat_root_cat_object_map.sql

alter table content_sections drop content_expiration_digest_id;
drop table ct_item_file_attachments;
drop table parameterized_privileges;
create index agentport_superport_id_idx on agentportlets(superportlet_id);
create index init_reqs_reqd_init_idx on init_requirements(required_init);

-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
\i ../postgres/upgrade/6.0.1-6.1.0/table-acs_privilege_hierarchy.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-acs_privilege_hierarchy.sql
\i ../postgres/upgrade/6.0.1-6.1.0/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_privileges.sql
\i ../postgres/upgrade/6.0.1-6.1.0/comment-dnm_privileges.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql
\i ../postgres/upgrade/6.0.1-6.1.0/comment-dnm_privilege_col_map.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy_map.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy.sql
\i ../postgres/upgrade/6.0.1-6.1.0/comment-dnm_privilege_hierarchy.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_permissions.sql
\i ../postgres/upgrade/6.0.1-6.1.0/comment-dnm_permissions.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-dnm_permissions.sql

\i ../postgres/upgrade/6.0.1-6.1.0/package-dnm_privileges.sql
\i ../postgres/upgrade/6.0.1-6.1.0/insert-acs_privilege_hierarchy.sql
\i ../postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql

-- Object context denormalization
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_object_1_granted_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_object_grants.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-dnm_granted_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_ungranted_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-dnm_ungranted_context.sql

\i ../postgres/upgrade/6.0.1-6.1.0/insert-dnm_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/package-dnm_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_context.sql

-- Party denormalization
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql
\i ../postgres/upgrade/6.0.1-6.1.0/index-dnm_group_membership.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-dnm_party_grants.sql
\i ../postgres/upgrade/6.0.1-6.1.0/package-dnm_parties.sql
\i ../postgres/upgrade/6.0.1-6.1.0/insert-dnm_group_membership.sql
\i ../postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_parties.sql

\i ../postgres/upgrade/6.0.1-6.1.0/triggers-dnm_privileges.sql
\i ../postgres/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql
\i ../postgres/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql

create index dnm_group_membership_grp_idx on dnm_group_membership(pd_group_id);
create index dnm_ungranted_context_obj_idx on dnm_ungranted_context(object_id);

drop index dnm_gc_uk;
drop index dnm_o1gc_necid_oid;
drop index dnm_o1gc_uk1;
drop index dnm_ungranted_context_un;

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

create or replace function temp_drop_objects() returns boolean as '
declare
  v_exists boolean;
begin
  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''all_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop view all_context_non_leaf_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''granted_trans_context_index'';

  if (v_exists) then
    execute ''drop view granted_trans_context_index cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''granted_trans_context_map'';

  if (v_exists) then
    execute ''drop view granted_trans_context_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''ungranted_trans_context_index'';

  if (v_exists) then
    execute ''drop view ungranted_trans_context_index cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''ungranted_trans_context_map'';

  if (v_exists) then
    execute ''drop view ungranted_trans_context_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''object_context_trans_map'';

  if (v_exists) then
    execute ''drop view object_context_trans_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''granted_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop table granted_context_non_leaf_map'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''ungranted_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop table ungranted_context_non_leaf_map'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''object_grants'';

  if (v_exists) then
    execute ''drop table object_grants'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''context_child_counts'';

  if (v_exists) then
    execute ''drop table context_child_counts'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''object_context_map'';

  if (v_exists) then
    execute ''drop table object_context_map'';
  end if;

  return TRUE;

end;
' language 'plpgsql';

select temp_drop_objects();
drop function temp_drop_objects();

commit;
