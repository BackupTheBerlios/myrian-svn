--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- Copyright (C) 2001 Arsdigita Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/permissions-4.8.11-5.0.1.sql#1 $

-- Add cascading deletes to grantee_id and privilege columns
alter table object_context
  drop constraint object_context_context_id_fk;

alter table object_context 
  add constraint object_context_context_id_fk
      foreign key (context_id) references acs_objects(object_id)
      on delete cascade;

drop index object_context_context_id_idx;
drop index ocm_context_id_idx;
