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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/index-dnm_group_membership.sql#1 $
-- $DateTime: 2004/01/21 13:38:43 $
-- autor: Aram Kananov <aram@kananov.com>

-- query to speed up reverse lookup. i.e by pd_member_id
create unique index dnm_group_membership_mem_idx on dnm_group_membership (pd_member_id, pd_group_id);
