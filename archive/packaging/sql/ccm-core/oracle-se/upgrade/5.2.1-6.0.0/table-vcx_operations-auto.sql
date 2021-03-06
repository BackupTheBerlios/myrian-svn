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
-- $Id: //core-platform/test-packaging/sql/ccm-core/oracle-se/upgrade/5.2.1-6.0.0/table-vcx_operations-auto.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

create table vcx_operations (
    id INTEGER not null
        constraint vcx_operations_id_p_p2kfb
          primary key,
    change_id INTEGER,
        -- referential constraint for change_id deferred due to circular dependencies
    event_type_id INTEGER not null,
        -- referential constraint for event_type_id deferred due to circular dependencies
    attribute VARCHAR(200) not null,
    subtype INTEGER not null,
    class_id INTEGER not null
        -- referential constraint for class_id deferred due to circular dependencies
);
