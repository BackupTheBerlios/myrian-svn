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
-- $Id: //core-platform/proto/sql/default/kernel/view-group_member_trans_map.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

--       IF user u is a direct member of group B  
--          AND
--          (A,B) is in group_subgroup_trans_map
--       THEN 
--          this view will contain (A,u).  
create view group_member_trans_map
as select group_id, member_id from group_member_trans_index;
