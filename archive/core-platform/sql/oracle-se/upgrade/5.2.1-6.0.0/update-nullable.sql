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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/5.2.1-6.0.0/update-nullable.sql#1 $
-- $DateTime: 2003/07/21 01:44:28 $

--alter table acs_stylesheet_node_map modify (
--    stylesheet_id not null,
--    node_id not null
--);

alter table cat_categories modify (
    abstract_p not null,
    enabled_p not null
);

alter table portlets modify (
    portal_id not null
);

alter table cat_categories modify (
    default_ancestors varchar2(3209)
);

drop table cw_process_task_map;
