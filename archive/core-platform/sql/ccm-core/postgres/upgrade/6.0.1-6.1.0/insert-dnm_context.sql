--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/insert-dnm_context.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

insert into dnm_object_1_granted_context (pd_object_id, pd_context_id, pd_non_effective_context_id )
  values (0,0,0);

insert into dnm_object_grants values (0,1);

insert into dnm_granted_context (pd_object_id, pd_context_id, pd_dummy_flag) values (0,0,1);

