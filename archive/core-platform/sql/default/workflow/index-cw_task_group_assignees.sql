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
-- $Id: //core-platform/dev/sql/default/workflow/index-cw_task_group_assignees.sql#2 $
-- $DateTime: 2003/08/15 13:46:34 $

create index CW_TASK_GRP_ASSGNS_GRP_ID_idx on CW_TASK_GROUP_ASSIGNEES(GROUP_ID);
