--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2002/07/18 $
--

create table t_sql_operation (
	big_integer numeric(38),
	big_decimal float,
	boolean numeric(1),
	byte char,
	single_char char,
	multi_char char(3),
	date_col date,
	double_col double precision,
	float_col float,
	int_col int,
	long_col int,
	short_col smallint,
	string_clob clob,
	string_normal varchar(10),
	byte_array_blob blob,
	byte_array char(20));

begin
	insert into t_sql_operation values(
42, 
139.93213, 
1, 
7,
'a', 
'bcd', 
TO_DATE('January 15, 1989, 11:00 A.M.','Month dd, YYYY, HH:MI A.M.'),
8883951.12341, 
739.31431, 
5026,
99999999, 
100, 
'clob column!', 
'normal', 
'12345', 
'normal array');


insert into t_sql_operation values(
null, 
null, 
0, 
null,
null, 
null, 
null,
null, 
null, 
null,
null, 
null, 
null, 
null, 
null, 
null);


    commit;
end;
/
show errors
