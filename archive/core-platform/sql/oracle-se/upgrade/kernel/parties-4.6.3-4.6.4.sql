--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
--
--
-- @author oumi@arsdigita.com
-- @version-id $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/parties-4.6.3-4.6.4.sql#2 $
--
-- FILE UPDATED: kernel/sql/oracle-se/parties-create.sql   --
-- CHANGE: added indexes                                   --
--                                                         --

create unique index gmti_member_group_idx 
    on group_member_trans_index (member_id, group_id);

create unique index gsti_subgroup_group_idx
    on group_subgroup_trans_index (subgroup_id, group_id);

create index parties_primary_email_idx
    on parties (primary_email);

