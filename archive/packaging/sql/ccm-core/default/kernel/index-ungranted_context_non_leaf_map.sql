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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/kernel/index-ungranted_context_non_leaf_map.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $



-------------
-- INDEXES --
-------------

---- For some reason, this index results in bad oracle errors for
---- the triggers that add/remove contexts.
---- It doesn't seem to impact performance enough to try to make
---- the triggers work with this index, so for now, we'll just
---- leave out the index.
--
-- create unique index ucnlm_context_obj_idx
--      on ungranted_context_non_leaf_map (implied_context_id, object_id);
