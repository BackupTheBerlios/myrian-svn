--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/data-operation-test.ora.sql#1 $
-- $DateTime: 2004/06/07 13:49:55 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2004/06/07 $
--

create or replace procedure DataOperationProcedure as
    my_variable	integer;
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors


create or replace function DataOperationFunction 
return varchar
is 
  toReturn varchar(300);
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into toReturn from t_data_query;   
   return toReturn;
end;
/
show errors

create or replace procedure DataOperationProcWithOut(v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithOut(v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithInOut(
       v_old_id IN varchar,
       v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select v_old_id, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithInOutInt(
       v_old_id IN Integer,
       v_new_id OUT Integer) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select v_old_id, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithDates(
       v_id_to_update IN Integer,
       v_old_date IN Date,
       v_new_date OUT Date) 
as 
begin
   update t_data_query set action_time = v_old_date
   where entry_id = v_id_to_update;
   select max(action_time) into v_new_date from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcedureWithArgs(v_priority in integer)
as
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, v_priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors

create or replace procedure DataOperationProcedureOneArg(v_description in integer)
as
begin
   insert into t_data_query (entry_id, action, priority, action_time, description)
   select entry_id + 1, action, priority, action_time, v_description 
   from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors

create or replace procedure PLSQLWithArbitraryArgs(v_arg1 in integer, 
       v_arg2 in integer, 
       v_arg3 in integer default null, 
       v_arg4 in integer default null, 
       v_arg5 in integer default null)
as
begin
        insert into PLSQLTestTable 
        values 
        (v_arg1, v_arg2, v_arg3, v_arg4, v_arg5);
end;
/
show errors
