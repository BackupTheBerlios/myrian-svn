--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/proto/test/sql/postgres/persistence/data-operation-test.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2002/11/27 $
--

create or replace function DataOperationProcedure() returns integer 
as '
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   return 1;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationFunction() returns varchar
as '
declare
  toReturn varchar(300);
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into toReturn from t_data_query;   
   return toReturn;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcWithOut(varchar) returns varchar
as '
declare v_new_id varchar;
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
   return v_new_id;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcWithInOut(integer) returns varchar
as '
declare 
    v_new_id varchar;
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select $1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select cast(max(entry_id) as varchar) into v_new_id from t_data_query;   
   return v_new_id;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcWithInOutInt(integer) returns integer
as '
declare 
    v_new_id integer;
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select $1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
   return v_new_id;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcWithDates(integer, timestamp)
       returns timestamp
as '
declare 
   v_new_date timestamp;
begin
   update t_data_query set action_time = $2
   where entry_id = $1;
   select max(action_time) into v_new_date from t_data_query;   
   return v_new_date;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcedureWithArgs(integer) returns integer
as '
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, $1, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   return 1;
end;
' LANGUAGE 'plpgsql';


create or replace function DataOperationProcedureOneArg(varchar) returns integer
as '
begin
   insert into t_data_query (entry_id, action, priority, action_time, description)
   select entry_id + 1, action, priority, action_time, $1
   from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   return 1;
end;
' LANGUAGE 'plpgsql';


create or replace function PLSQLWithArbitraryArgs(integer, integer, integer, integer, integer) returns integer
as '
begin
        insert into PLSQLTestTable 
        values 
        ($1, $2, $3, $4, $5);
        return 1;
end;
' LANGUAGE 'plpgsql';
