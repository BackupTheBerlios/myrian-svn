--
-- Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
