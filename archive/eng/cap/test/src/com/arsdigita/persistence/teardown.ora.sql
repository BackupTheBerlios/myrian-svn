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
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2004/08/30 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.


@@ ../../default/persistence/teardown.sql

drop table t_data_query;
drop table t_sql_operation;

drop procedure DataOperationProcedure;
drop function DataOperationFunction;
drop function DataOperationProcWithReturn;
drop procedure DataOperationProcWithOut;
drop procedure DataOperationProcWithInOut;
drop procedure DataOperationProcWithInOutInt;
drop procedure DataOperationProcWithDates;
drop procedure DataOperationProcedureWithArgs;
drop procedure PLSQLWithArbitraryArgs;
drop procedure DataOperationProcedureOneArg;
