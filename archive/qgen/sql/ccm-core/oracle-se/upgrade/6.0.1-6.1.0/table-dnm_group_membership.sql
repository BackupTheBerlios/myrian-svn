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
-- $Id: //core-platform/test-qgen/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

create table dnm_group_membership (
  pd_group_id integer 
    constraint dnm_group_membership_gid_nn not null, 
  pd_member_id integer 
    constraint dnm_group_membership_mid_nn not null,
  pd_member_is_user integer default 0 
    constraint dnm_group_membership_mis_nn not null, 
  pd_self_map       integer default 0
    constraint dnm_group_membership_f_nn not null,
  constraint  dnm_group_membership_pk primary key (pd_group_id, pd_member_id)
) ;
-- TODO: should i convert it to IOT ?
-- organization index including pd_member_id overflow;



