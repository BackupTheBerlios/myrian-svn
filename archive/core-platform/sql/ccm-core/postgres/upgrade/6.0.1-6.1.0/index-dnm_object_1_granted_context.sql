--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create unique index dnm_o1gc_uk1 on dnm_object_1_granted_context (pd_object_id, pd_context_id);
create index dnm_o1gc_uk on dnm_object_1_granted_context (pd_context_id);
create index dnm_o1gc_nfci on dnm_object_1_granted_context (pd_non_effective_context_id);
create unique index dnm_o1gc_necid_oid on dnm_object_1_granted_context (pd_non_effective_context_id, pd_object_id);
