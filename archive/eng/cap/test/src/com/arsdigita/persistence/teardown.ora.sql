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
-- $Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/teardown.ora.sql#1 $
-- $DateTime: 2004/06/07 13:49:55 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2004/06/07 $
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
