--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/oracle-se/kernel/parties-drop.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


--
-- //enterprise/kernel/dev/kernel/sql/parties-drop.sql 
--
-- @author oumi@arsdigita.com
-- @creation-date 2001-05-10
-- @cvs-id $Id: //core-platform/dev/sql/oracle-se/kernel/parties-drop.sql#3 $
--

drop table user_authentication;

drop table roles;

drop table group_member_trans_index;
drop table group_subgroup_trans_index;

drop view party_member_trans_map;
drop view group_member_trans_map;
drop view group_subgroup_trans_map;

drop table group_member_map;
drop table group_subgroup_map;
drop table groups;
drop table users;
drop table person_names;

-- Can't drop party_email_map til this foreign key is gone
alter table parties drop constraint parties_primary_email_fk;

drop table party_email_map;
drop table parties;
drop table email_addresses;

drop package parties_denormalization;
