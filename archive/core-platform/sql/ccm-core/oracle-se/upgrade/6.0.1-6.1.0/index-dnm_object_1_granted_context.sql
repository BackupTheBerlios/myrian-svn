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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

create unique index dnm_o1gc_uk1 on dnm_object_1_granted_context (pd_object_id, pd_context_id);
create index dnm_o1gc_uk on dnm_object_1_granted_context (pd_context_id);
create index dnm_o1gc_nfci on dnm_object_1_granted_context (pd_non_effective_context_id);
create unique index dnm_o1gc_necid_oid on dnm_object_1_granted_context (pd_non_effective_context_id, pd_object_id);
