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
-- $Id: //core-platform/proto/sql/default/kernel/view-object_context_trans_map.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

-- Use with caution.  For some objects, this view will contain
-- the implicit mapping (object_id, object_id, 0).  For some objects this
-- view WILL NOT contain this implicit mapping.
create view object_context_trans_map
as select object_id, implied_context_id, n_generations
   from granted_trans_context_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_trans_context_map;
