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
-- $Id: //core-platform/test-qgen/sql/ccm-core/postgres/kernel/table-dnm_ungranted_context.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

create table dnm_ungranted_context (
   granted_context_id integer not null,
   object_id integer not null ,
   ancestor_id integer not null,
   n_generations integer not null
      constraint dnm_ungranted_ctx_n_gen_chk check (n_generations >= 0),
   constraint dnm_ungranted_context_pk primary key (ancestor_id, object_id)
);
