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
-- $Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/data-query-test.ora.sql#1 $
-- $DateTime: 2004/05/05 16:10:50 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2004/05/05 $
--

create table t_data_query (
    entry_id    integer constraint t_data_query_entry_id_pk primary key,
    action      varchar(100) not null,
    description varchar(4000),
    priority    integer not null,
    action_time date not null
);

begin
    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (0, 'read', 'Read item 0', 1, to_date('1976-12-24', 'YYYY-MM-DD') + 0);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (1, 'read', 'Read item 1', 2, to_date('1976-12-24', 'YYYY-MM-DD') + 4);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (2, 'read', 'Read item 2', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 8);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (3, 'read', 'Read item 3', 7, to_date('1976-12-24', 'YYYY-MM-DD') + 10);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (4, 'read', 'Read item 4', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 11);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (5, 'write', 'Wrote item 5', 9, to_date('1976-12-24', 'YYYY-MM-DD') + 11.3);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (6, 'write', 'Wrote item 6', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 11.79);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (7, 'write', 'Wrote item 7', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 24.3);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (8, 'write', 'Wrote item 8', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 28.119);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (9, 'write', 'Wrote item 9', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 31);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (10, 'delete', 'Deleted item 10', 7, to_date('1976-12-24', 'YYYY-MM-DD') + 47);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (11, 'delete', 'Deleted item 11', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 92);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (12, 'delete', 'Deleted item 12', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 102);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (13, 'delete', 'Deleted item 13', 9, to_date('1976-12-24', 'YYYY-MM-DD') + 228);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (14, 'delete', 'Deleted item 14', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 228);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (15, 'create', 'Created item 15', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 230);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (16, 'create', 'Created item 16', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 241);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (17, 'create', 'Created item 17', 4, to_date('1976-12-24', 'YYYY-MM-DD') + 281);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (18, 'create', 'Created item 18', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 384);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (19, 'create', 'Created item 19', 8, to_date('1976-12-24', 'YYYY-MM-DD') + 12203);

    commit;
end;
/
show errors


create or replace function DataOperationProcWithReturn(v_entry_id in integer) return number
is
   v_priority number;
begin
   select priority into v_priority from t_data_query 
    where entry_id = v_entry_id;
   return v_priority;
end;
/
show errors
