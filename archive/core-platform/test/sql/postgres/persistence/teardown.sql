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
-- $Id: //core-platform/dev/test/sql/postgres/persistence/teardown.sql#12 $
-- $DateTime: 2002/10/16 14:12:35 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #12 $ $Date: 2002/10/16 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.

--@@ test-drop.sql
drop table t_user_group_map;
drop table t_article_image_map;
drop table t_articles;
drop table t_colors;
drop table t_groups;
drop table t_images;
drop table t_line_items;
drop table t_nodes;
drop table t_order_other_item_map;
drop table t_orders_ext;
drop table t_other_items;
drop table t_orders;
drop table t_parties;
drop table t_users; 

@@ ../../default/persistence/teardown.sql


drop table t_datatypes;
drop table t_data_query;

drop table t_sql_operation;

drop function DataOperationProcedure();
drop function DataOperationFunction();
drop function DataOperationProcWithReturn(integer);
drop function DataOperationProcWithOut(varchar);
drop function DataOperationProcWithInOut(integer);
drop function DataOperationProcWithInOutInt(integer);
drop function DataOperationProcWithDates(integer, timestamp);
drop function DataOperationProcedureOneArg(varchar);
drop function DataOperationProcedureWithArgs(integer);
drop function PLSQLWithArbitraryArgs(integer, integer, integer, integer, integer);
