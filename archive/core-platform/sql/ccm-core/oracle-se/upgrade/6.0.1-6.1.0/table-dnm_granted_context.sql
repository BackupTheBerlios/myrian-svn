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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

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
