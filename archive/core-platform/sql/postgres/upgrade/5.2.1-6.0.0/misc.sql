--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/postgres/upgrade/5.2.1-6.0.0/misc.sql#4 $
-- $DateTime: 2003/08/15 00:21:06 $

drop function last_attr_value(varchar,integer);

drop table cw_process_task_map;

update cw_tasks set is_active = 0 where is_active is null;

alter table cat_categories add url varchar (200);

drop trigger acs_permissions_cascade_del_tr on acs_objects;
drop function acs_permissions_cascade_del_fn();

-- Fix Constraint Ordering
-- NOTE: The following ddl assumes that no tables have a referential constraint against one
--       of these tables.  This holds for core and *should* hold in general as these tables
--       are unlikely cadidates for foreign keys.
--------------------------------------------------------------------------------
alter table acs_permissions drop constraint acs_per_gra_id_obj_id__p_lrweb;
alter table acs_permissions add
    constraint acs_per_gra_id_obj_id__p_lrweb
      primary key(object_id, grantee_id, privilege);

alter table apm_package_type_listener_map drop constraint apm_pac_typ_lis_map_li_p_6_z6o;
alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_li_p_6_z6o
      primary key(package_type_id, listener_id);

alter table cw_task_group_assignees drop constraint task_group_assignees_pk;
alter table cw_task_group_assignees add
    constraint cw_tas_gro_ass_gro_id__p_0bqv_
        primary key(group_id, task_id);

alter table group_member_map drop constraint grou_mem_map_gro_id_me_p_9zo_i;
alter table group_member_map add
    constraint grou_mem_map_gro_id_me_p_9zo_i
      primary key(member_id, group_id);

alter table group_subgroup_map drop constraint grou_sub_map_gro_id_su_p_8caa0;
alter table group_subgroup_map add
    constraint grou_sub_map_gro_id_su_p_8caa0
      primary key(subgroup_id, group_id);

alter table parameterized_privileges drop constraint para_pri_bas_pri_par_k_p_a1rpb;
alter table parameterized_privileges add
    constraint para_pri_bas_pri_par_k_p_a1rpb
      primary key(param_key, base_privilege);

alter table party_email_map drop constraint part_ema_map_ema_add_p_p_px7u4;
alter table party_email_map add
    constraint part_ema_map_ema_add_p_p_px7u4
      primary key(party_id, email_address);

alter table site_nodes add
    constraint site_node_nam_paren_id_u_a3b4a
      unique(parent_id, name);

-- These constraints changed name
update pg_constraint set conname = 'cat_categori_catego_id_p_yeprq' where UPPER(conname) = UPPER('cat_categories_pk');
update pg_class set relname = 'cat_categori_catego_id_p_yeprq' where relname = 'cat_categories_pk';

update pg_constraint set conname = 'cw_tas_dep_dep_tas_id__p_hdzws' where UPPER(conname) = UPPER('task_dependencies_pk');
update pg_class set relname = 'cw_tas_dep_dep_tas_id__p_hdzws' where relname = 'task_dependencies_pk';

update pg_constraint set conname = 'cw_tas_use_ass_tas_id__p_vsdyq' where UPPER(conname) = UPPER('task_user_assignees_pk');
update pg_class set relname = 'cw_tas_use_ass_tas_id__p_vsdyq' where relname = 'task_user_assignees_pk';

-- oracle compatibility

-- Replacements for PG's special operators that cause persistence's
-- SQL parser to barf

create or replace function bitand(integer, integer) returns integer as '
begin
    return $1 & $2;
end;
' language 'plpgsql';

create or replace function bitor(integer, integer) returns integer as '
begin
    return $1 | $2;
end;
' language 'plpgsql';

create or replace function bitxor(integer, integer) returns integer as '
begin
    return $1 # $2;
end;
' language 'plpgsql';

create or replace function bitneg(integer) returns integer as '
begin
    return ~$1;
end;
' language 'plpgsql';

