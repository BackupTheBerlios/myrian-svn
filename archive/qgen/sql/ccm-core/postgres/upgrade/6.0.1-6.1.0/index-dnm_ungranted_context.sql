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
-- $Id: //core-platform/test-qgen/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/index-dnm_ungranted_context.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

create index dnm_ungranted_context_gctx_idx on dnm_ungranted_context(granted_context_id);
create unique index dnm_ungranted_context_un on dnm_ungranted_context(object_id, ancestor_id);
create index dnm_ungranted_context_anc_idx on dnm_ungranted_context(ancestor_id);
