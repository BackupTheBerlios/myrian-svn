--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table dnm_granted_context (
   pd_object_id integer not null
   constraint dnm_gc_obj_fk references dnm_object_1_granted_context
   constraint dnm_gc_obj1_fk references dnm_object_grants,
   pd_context_id integer not null,
   pd_dummy_flag integer default 0 not null,
   constraint dnm_object_grants_dummy_ck
     check ( (pd_object_id = pd_context_id and pd_dummy_flag = 1)
             or (pd_object_id != pd_context_id and pd_dummy_flag = 0) ),
   constraint dnm_gc primary key (pd_context_id, pd_object_id)
) ;
-- TODO: create separate implementation for oracle with  organization index and normal for postgres;

create index dgc_ctx_idx on dnm_granted_context( pd_context_id);
create index dgc_obj_idx on dnm_granted_context( pd_object_id);