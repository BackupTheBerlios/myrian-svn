--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/kernel/stats-dnm_tables.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $
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
