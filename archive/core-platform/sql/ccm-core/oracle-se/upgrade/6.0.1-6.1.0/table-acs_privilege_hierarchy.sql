--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/table-acs_privilege_hierarchy.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table acs_privilege_hierarchy (
    privilege          varchar(100)  not null
                       constraint acs_privilege_hier_priv_fk
                       references acs_privileges on delete cascade,
    child_privilege    varchar(100) not null
                       constraint acs_privilege_hier_chld_prv_fk
                       references acs_privileges on delete cascade,
                       constraint acs_privilege_hier_pk
                       primary key (privilege, child_privilege)
);
