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
-- $Id: //core-platform/dev/sql/ccm-core/upgrade/oracle-se-6.0.1-6.1.0.sql#7 $
-- $DateTime: 2004/03/21 16:01:53 $

PROMPT Red Hat WAF 6.0.1 -> 6.1.0 Upgrade Script (Oracle)

@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-admin_app-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-agentportlets-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql 
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-init_requirements-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-inits-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-keystore-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-lucene_ids-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-sitemap_app-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-webapps-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/deferred.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-host-unique-index.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-cat_root_cat_object_map.sql

alter table cms_mime_extensions (mime_type null);
alter table cms_mime_extensions add constraint
  cms_mim_exten_mim_type_f_7pwwd foreign key(mime_type)
  references cms_mime_types(mime_type);

create index AGENTPORT_SUPERPORT_ID_IDX on AGENTPORTLETS(SUPERPORTLET_ID);
create index INIT_REQS_REQD_INIT_IDX on INIT_REQUIREMENTS(REQUIRED_INIT);

-- insert mime type file extensions
@@ ../default/upgrade/6.0.1-6.1.0/insert-cms_mime_extensions.sql

-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privilege_col_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_privileges.sql

@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql

-- Object context denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_object_1_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_object_grants.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_context.sql

-- Party denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_party_grants.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_parties.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_parties.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql

drop package permission_denormalization;

drop view all_context_non_leaf_map;
drop view granted_trans_context_index;
drop view granted_trans_context_map;
drop view ungranted_trans_context_index;
drop view ungranted_trans_context_map;
drop view object_context_trans_map;

declare
  v_exists char(1);
begin

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'granted_context_non_leaf_map';

  if (v_exists = '1') then
    execute immediate 'drop table granted_context_non_leaf_map';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'ungranted_context_non_leaf_map';

  if (v_exists = '1') then
    execute immediate 'drop table ungranted_context_non_leaf_map';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'object_grants';

  if (v_exists = '1') then
    execute immediate 'drop table object_grants';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'context_child_counts';

  if (v_exists = '1') then
    execute immediate 'drop table context_child_counts';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'object_context_map';

  if (v_exists = '1') then
    execute immediate 'drop table object_context_map';
  end if;

end;
/
show errors;
