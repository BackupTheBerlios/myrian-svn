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
-- //enterprise/kernel/dev/kernel/sql/permissions-create.sql
--
-- @author phong@arsdigita.com
-- @creation-date 2001-05-21
-- @cvs-id $Id: //core-platform/dev/sql/oracle-se/kernel/permissions-drop.sql#3 $
--

drop table granted_context_non_leaf_map;
drop table ungranted_context_non_leaf_map;
drop table object_grants;

--drop table context_child_counts;
drop table object_context_map;
drop table object_context;
drop table acs_permissions;
drop table parameterized_privileges;
drop table acs_privileges;

drop view all_context_non_leaf_map;

drop package permission_denormalization;

-- deprecated views
drop view object_context_trans_index;
drop view ungranted_trans_context_index;
drop view granted_trans_context_index;
drop view object_context_trans_map;
drop view ungranted_trans_context_map;
drop view granted_trans_context_map;
