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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.2.1-6.0.0/table-vcx_java_classes-auto.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

create table vcx_java_classes (
    id INTEGER not null
        constraint vcx_java_classes_id_p_pz4xq
          primary key,
    name VARCHAR(400) not null
);
