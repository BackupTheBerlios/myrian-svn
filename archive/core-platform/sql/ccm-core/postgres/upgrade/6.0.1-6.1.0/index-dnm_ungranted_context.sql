--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/index-dnm_ungranted_context.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

create index dnm_ungranted_context_gctx_idx on dnm_ungranted_context(granted_context_id);
create unique index dnm_ungranted_context_un on dnm_ungranted_context(object_id, ancestor_id);
create index dnm_ungranted_context_anc_idx on dnm_ungranted_context(ancestor_id);
