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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/table-dnm_granted_context.sql#2 $
-- $DateTime: 2004/03/04 13:28:40 $
-- autor: Aram Kananov <aram@kananov.com>

create table dnm_granted_context (
   pd_object_id integer not null
   constraint dnm_gc_obj1_fk references dnm_object_grants,
   pd_context_id integer not null,
   pd_dummy_flag integer default 0 not null,
   constraint dnm_object_grants_dummy_ck
     check ( (pd_object_id = pd_context_id and pd_dummy_flag = 1)
             or (pd_object_id != pd_context_id and pd_dummy_flag = 0) ),
   constraint dnm_gc primary key (pd_context_id, pd_object_id)
) ;
-- TODO: does it make sence to create separate implementation for oracle with  
--  organization index and normal for postgres?
