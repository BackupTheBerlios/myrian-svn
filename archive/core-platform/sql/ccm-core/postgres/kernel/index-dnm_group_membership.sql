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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/kernel/index-dnm_group_membership.sql#1 $
-- $DateTime: 2004/02/09 08:27:35 $
-- autor: Aram Kananov <aram@kananov.com>

create index dnm_group_membership_mem_idx on dnm_group_membership (pd_member_id);
create index dnm_group_membership_grp_idx on dnm_group_membership (pd_group_id);
