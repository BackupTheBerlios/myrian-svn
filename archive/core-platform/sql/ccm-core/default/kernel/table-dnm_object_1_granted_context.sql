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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/table-dnm_object_1_granted_context.sql#1 $
-- $DateTime: 2004/01/15 10:03:14 $
-- autor: Aram Kananov <aram@kananov.com>


create table dnm_object_1_granted_context (
   pd_object_id integer not null,
   constraint dnm_o1gc_obj_pk primary key (pd_object_id),
   pd_context_id integer not null ,
   pd_non_effective_context_id integer not null
)
;
