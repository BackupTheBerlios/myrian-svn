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
-- $Id: //core-platform/dev/sql/postgres/upgrade/5.2.1-6.0.0/misc.sql#2 $
-- $DateTime: 2003/08/12 19:06:12 $

drop function last_attr_value(varchar,integer);

drop table cw_process_task_map;

update cw_tasks set is_active = 0 where is_active is null;

commit;

alter table cat_categories add url varchar (200);

drop trigger acs_permissions_cascade_del_tr on acs_objects;
drop function acs_permissions_cascade_del_fn();

