--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/kernel/stats-dnm_tables.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $
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
