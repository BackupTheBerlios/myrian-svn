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
-- $Id: //core-platform/dev/sql/postgres/upgrade/5.2.1-6.0.0/table-vcx_txns-auto.sql#3 $
-- $DateTime: 2003/08/15 17:18:56 $

create table vcx_txns (
    id INTEGER not null
        constraint vcx_txns_id_p_himn5
          primary key,
    modifying_ip VARCHAR(400),
    timestamp TIMESTAMPTZ not null,
    modifying_user INTEGER
        -- referential constraint for modifying_user deferred due to circular dependencies
);
