--
-- Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
-- This file sets up all the data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2004/08/30 $
--

-- The equivalent of oracle's dual table.
create view dual as select 1;

-- A pseudo replacement for nvl. It doesn't have the same semantics
-- since nvl isn't a function in oracle but an operator, but it will
-- suffice in a large number of cases.
create or replace function nvl(varchar, varchar) returns varchar as '
begin
    if $1 is null then
        return $2;
    else
        return $1;
    end if;
end;
' language 'plpgsql';

@@ data-operation-test.sql
@@ aggressive-test.sql
@@ data-query-test.sql
@@ sql-operation-test.sql
@@ data-operation-test.sql
