--
-- Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
--
-- This program is Open Source software; you can redistribute it and/or
-- modify it under the terms of the Open Software License version 2.1 as
-- published by the Open Source Initiative.
--
-- You should have received a copy of the Open Software License along
-- with this program; if not, you may obtain a copy of the Open Software
-- License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
-- or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
-- 3001 King Ranch Road, Ukiah, CA 95482.


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2004/08/30 $
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
