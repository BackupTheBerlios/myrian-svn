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
-- $Id: //core-platform/dev/test/sql/postgres/persistence/data-query-test.sql#5 $
-- $DateTime: 2002/10/16 14:12:35 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #5 $ $Date: 2002/10/16 $
--

create table t_data_query (
    entry_id    integer constraint t_data_query_entry_id_pk primary key,
    action      varchar(100) not null,
    description varchar(4000),
    priority    integer not null,
    action_time timestamp
);

insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values  
    (0, 'read', 'Read item 0', 1, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('0 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (1, 'read', 'Read item 1', 2, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('4 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (2, 'read', 'Read item 2', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('8 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (3, 'read', 'Read item 3', 7, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('10 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (4, 'read', 'Read item 4', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('11 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (5, 'write', 'Wrote item 5', 9, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('11.3 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (6, 'write', 'Wrote item 6', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('11.79 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (7, 'write', 'Wrote item 7', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('24.3 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (8, 'write', 'Wrote item 8', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('28.119 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (9, 'write', 'Wrote item 9', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('31 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (10, 'delete', 'Deleted item 10', 7, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('47 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (11, 'delete', 'Deleted item 11', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('92 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (12, 'delete', 'Deleted item 12', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('102 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (13, 'delete', 'Deleted item 13', 9, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('228 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (14, 'delete', 'Deleted item 14', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('228 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (15, 'create', 'Created item 15', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('230 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (16, 'create', 'Created item 16', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('241 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (17, 'create', 'Created item 17', 4, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('281 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (18, 'create', 'Created item 18', 3, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('384 days'::timespan));

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (19, 'create', 'Created item 19', 8, datetime('1976-12-24'::datetime, '00:00'::time) + reltime('12203 days'::timespan));


create or replace function DataOperationProcWithReturn(integer) returns integer
as '
declare 
   v_priority integer;
begin
   select priority into v_priority from t_data_query 
    where entry_id = $1;
   return v_priority;
end;
' LANGUAGE 'plpgsql';

