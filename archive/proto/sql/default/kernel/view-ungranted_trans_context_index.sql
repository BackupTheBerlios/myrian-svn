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
-- $Id: //core-platform/proto/sql/default/kernel/view-ungranted_trans_context_index.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

create view ungranted_trans_context_index
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, ungranted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select o.object_id, o.context_id, 1
from object_context_map o left outer join object_grants g
on o.object_id = g.object_id and g.object_id=null;
