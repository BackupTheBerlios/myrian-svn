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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.2.1-6.0.0/table-vcx_event_types-auto.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

create table vcx_event_types (
    id INTEGER not null
        constraint vcx_event_types_id_p_zc1g6
          primary key,
    name VARCHAR(40) not null
);
