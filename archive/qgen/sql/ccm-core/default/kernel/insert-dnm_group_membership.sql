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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/kernel/insert-dnm_group_membership.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

--hack for kernel.SystemParty
insert into dnm_group_membership (pd_group_id, pd_member_id, pd_member_is_user, pd_self_map) 
       values (-204,-204,1,1);
