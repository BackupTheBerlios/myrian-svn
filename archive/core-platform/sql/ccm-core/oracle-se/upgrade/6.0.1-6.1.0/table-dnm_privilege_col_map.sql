--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

create table dnm_privilege_col_map ( 
    pd_privilege        varchar(100) 
        constraint dnm_priv_col_map_priv_nn not null
        constraint dnm_priv_col_map_pk primary key,
    column_name varchar(100) 
        constraint dnm_priv_col_map_cname_nn not null 
        constraint dnm_priv_col_map_un unique
);
