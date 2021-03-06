--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-qgen/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql#1 $
-- $DateTime: 2004/01/29 12:35:08 $
-- autor: Aram Kananov <aram@kananov.com>

create table dnm_privilege_col_map ( 
    pd_privilege        varchar(100) 
        constraint dnm_priv_col_map_priv_nn not null
        constraint dnm_priv_col_map_pk primary key,
    column_name varchar(100) 
        constraint dnm_priv_col_map_cname_nn not null 
        constraint dnm_priv_col_map_un unique
);
