--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/view-group_subgroup_trans_map.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $




-----------
-- VIEWS --
-----------

-- This view's implementation will change when we implement denormalizations.
--
--       If there is a path from group A to group B through the subgroup 
--       graph, then this view will contain (A,B).  Note that this implies
--       it would contain (A,A)
--
create view group_subgroup_trans_map
as select group_id, subgroup_id
   from group_subgroup_trans_index;
