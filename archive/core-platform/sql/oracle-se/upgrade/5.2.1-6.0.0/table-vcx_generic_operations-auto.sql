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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/5.2.1-6.0.0/table-vcx_generic_operations-auto.sql#2 $
-- $DateTime: 2003/08/15 13:46:34 $

create table vcx_generic_operations (
    id INTEGER not null
        constraint vcx_gener_operation_id_p_dfiws
          primary key,
        -- referential constraint for id deferred due to circular dependencies
    value VARCHAR(4000)
);
