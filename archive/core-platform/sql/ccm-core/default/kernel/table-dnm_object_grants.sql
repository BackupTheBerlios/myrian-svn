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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/table-dnm_object_grants.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table dnm_object_grants (
       pd_object_id            integer not null
       constraint dnm_object_grants_pk primary key,
       pd_n_grants             integer not null
       constraint dnm_object_grants_positive_ck  check (pd_n_grants >= 1),
       constraint dnm_object_grants_obj_fk foreign key (pd_object_id)
         references dnm_object_1_granted_context (pd_object_id)
);

-- TODO: create separate implementation for oracle with  organization index and normal for postgres;
