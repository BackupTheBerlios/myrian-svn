--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/5.0.0-5.1.0/create-fk-indices.sql#3 $
-- $DateTime: 2003/08/15 13:46:34 $

create index ACS_PERMISSIONS_PRIVILEGE_idx on ACS_PERMISSIONS(PRIVILEGE);
create index APM_PTYP_LSTNR_MP_LSTNR_ID_idx on APM_PACKAGE_TYPE_LISTENER_MAP(LISTENER_ID);
create index BEBOP_COMP_HRCHY_COMP_ID_idx on BEBOP_COMPONENT_HIERARCHY(COMPONENT_ID);
create index BEBOP_FRM_PRCSS_LSTNR_ID_idx on BEBOP_FORM_PROCESS_LISTENERS(LISTENER_ID);
create index BEBOP_LSTNR_MAP_LSTNR_ID_idx on BEBOP_LISTENER_MAP(LISTENER_ID);
create index CAT_CATCAT_MAP_RLTD_CAT_ID_idx on CAT_CATEGORY_CATEGORY_MAP(RELATED_CATEGORY_ID);
create index CAT_CAT_PURP_MAP_PURP_ID_idx on CAT_CATEGORY_PURPOSE_MAP(PURPOSE_ID);
create index CW_PROCESS_TSK_MAP_TSK_ID_idx on CW_PROCESS_TASK_MAP(TASK_ID);
create index CW_TASK_COMMENTS_TASK_ID_idx on CW_TASK_COMMENTS(TASK_ID);
create index CW_TASK_DEPS_DPNT_TSK_ID_idx on CW_TASK_DEPENDENCIES(DEPENDENT_TASK_ID);
create index CW_TASK_GRP_ASSGNS_GRP_ID_idx on CW_TASK_GROUP_ASSIGNEES(GROUP_ID);
create index CW_TASK_LSNRS_LSNR_TASK_ID_idx on CW_TASK_LISTENERS(LISTENER_TASK_ID);
create index CW_TASK_USR_ASSGNS_USR_ID_idx on CW_TASK_USER_ASSIGNEES(USER_ID);
create index G11N_CATALOGS_LOCALE_ID_idx on G11N_CATALOGS(LOCALE_ID);
create index G11N_LOC_CH_MAP_CHARSET_ID_idx on G11N_LOCALE_CHARSET_MAP(CHARSET_ID);
create index GROUP_MEMBER_MAP_MEMBER_ID_idx on GROUP_MEMBER_MAP(MEMBER_ID);
create index GROUP_SUBGRP_MAP_SUBGRP_ID_idx on GROUP_SUBGROUP_MAP(SUBGROUP_ID);
create index MESSAGES_OBJECT_ID_idx on MESSAGES(OBJECT_ID);
create index MESSAGE_THREADS_SENDER_idx on MESSAGE_THREADS(SENDER);
create index NT_QUEUE_PARTY_TO_idx on NT_QUEUE(PARTY_TO);
create index OBJECT_CONTEXT_CONTEXT_ID_idx on OBJECT_CONTEXT(CONTEXT_ID);
create index OBJECT_CONTEXT_MAP_CTX_ID_idx on OBJECT_CONTEXT_MAP(CONTEXT_ID);
create index PARAMETER_PRIV_BASE_PRIV_idx on PARAMETERIZED_PRIVILEGES(BASE_PRIVILEGE);
create index PL_US_CNTIES_ST_FIPS_CODE_idx on PL_US_COUNTIES(STATE_FIPS_CODE);
create index UNG_CTX_NLF_MP_IMPL_CTX_ID_idx on UNGRANTED_CONTEXT_NON_LEAF_MAP(IMPLIED_CONTEXT_ID);
create index VC_OBJECTS_MASTER_ID_idx on VC_OBJECTS(MASTER_ID);
create index VC_TRANSACTIONS_OBJECT_ID_idx on VC_TRANSACTIONS(OBJECT_ID);
