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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/kernel/view-all_context_non_leaf_map.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

create view all_context_non_leaf_map
as select object_id, implied_context_id, n_generations
   from granted_context_non_leaf_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_context_non_leaf_map;