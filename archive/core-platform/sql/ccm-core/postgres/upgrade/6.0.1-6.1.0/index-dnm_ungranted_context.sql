--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/index-dnm_ungranted_context.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create index dnm_ungranted_context_gctx_idx on dnm_ungranted_context(granted_context_id);
create unique index dnm_ungranted_context_un on dnm_ungranted_context(object_id, ancestor_id);
create index dnm_ungranted_context_anc_idx on dnm_ungranted_context(ancestor_id);
