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
-- This file contains the data model for the datatype test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2002/07/22 $
--

create table t_datatypes (
    id               integer constraint t_datatypes_row_id_pk primary key,
    j_big_integer    integer,
    j_big_decimal    numeric,
    j_boolean        char(1),
    j_byte           integer,
    j_character      char(1),
    j_date           date,
    j_double         numeric,
    j_float          numeric,
    j_integer        integer,
    j_long           integer,
    j_short          integer,
    j_string         varchar(4000)
);
--    j_blob           blob,
--    j_clob           clob
