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
-- $Id: //core-platform/test-packaging/sql/postgres/upgrade/5.2.1-6.0.0/table-vcx_blob_operations-auto.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table vcx_blob_operations (
    id INTEGER not null
        constraint vcx_blob_operations_id_p_85h09
          primary key,
        -- referential constraint for id deferred due to circular dependencies
    value BYTEA
);
