--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/upgrade/5.2.1-6.0.0/add-index-cw_task_listeners_tid_ltid.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $


alter table cw_task_listeners drop constraint task_listeners_pk;
alter table cw_task_listeners add
    constraint cw_tas_lis_lis_tas_id__p_cl43z
        primary key(listener_task_id, task_id);

create index cw_task_listeners_tid_ltid_idx on cw_task_listeners(task_id, listener_task_id);
