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
-- $Id: //core-platform/test-qgen/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/insert-dnm_context.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

insert into dnm_object_1_granted_context (pd_object_id, pd_context_id, pd_non_effective_context_id )
  values (0,0,0);

insert into dnm_object_grants values (0,1);

insert into dnm_granted_context (pd_object_id, pd_context_id, pd_dummy_flag) values (0,0,1);

