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
-- $Id: //core-platform/dev/sql/default/kernel/view-granted_trans_context_index.sql#4 $
-- $DateTime: 2003/08/15 13:46:34 $


-- These views are here to prevent old code from breaking.  These
-- views do not necessarily perform acceptably.

-- Create two special users for representing two logical groups.
-- The Public is a user that represents anyone in the system or anyone
-- that is not authenticated as a registered user.
-- Registerd Users is a user that represents all users registered in the system.

create view granted_trans_context_index
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, granted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select object_id, object_id, 0
from object_grants;
