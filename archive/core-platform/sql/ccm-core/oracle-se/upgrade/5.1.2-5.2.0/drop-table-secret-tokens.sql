--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/drop-table-secret-tokens.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace procedure ccm_drop_table_if_exist (v_table in VARCHAR)
is 
    table_count integer;
begin
    
    select count(*) into table_count
    from user_tables
    where upper(table_name) = upper(v_table);

    if (table_count = 1) then
      execute immediate 'drop table ' || v_table;
    end if;
end;
/
show errors;

begin
ccm_drop_table_if_exist ('secret_tokens');
end;
/

drop procedure ccm_drop_table_if_exist;
