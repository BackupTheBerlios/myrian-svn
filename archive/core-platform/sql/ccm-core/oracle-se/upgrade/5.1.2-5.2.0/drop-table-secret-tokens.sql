--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/drop-table-secret-tokens.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

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
