--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

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



