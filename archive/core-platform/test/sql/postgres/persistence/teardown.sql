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
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #5 $ $Date: 2002/07/22 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.

--@@ test-drop.sql

drop table t_user_group_map;

@@ ../../default/persistence/teardown.sql


--drop table t_datatypes;
drop table t_data_query;

--drop table t_nodes;
--drop table t_sql_operation;

--drop table t_article_image_map;
--drop table t_articles;
--drop table t_images;

--drop procedure DataOperationProcedure;
--drop function DataOperationFunction;
drop function DataOperationProcWithReturn(integer);
--drop procedure DataOperationProcWithOut;
--drop procedure DataOperationProcWithInOut;
--drop procedure DataOperationProcWithInOutInt;
--drop procedure DataOperationProcWithDates;
--drop procedure DataOperationProcedureWithArgs;
--drop procedure PLSQLWithArbitraryArgs;
--drop procedure DataOperationProcedureOneArg;
--drop table PLSQLTestTable;
