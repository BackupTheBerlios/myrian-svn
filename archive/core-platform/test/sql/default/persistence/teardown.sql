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
-- @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
-- @version $Revision: #4 $ $Date: 2002/07/23 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.


@@ test-drop.sql

drop table t_order_other_item_map;
drop table t_other_items;
drop table t_orders_ext;
drop table t_line_items;
drop table t_orders;

drop table t_nodes;

drop table t_groups;
drop table t_users;
drop table t_colors;
drop table t_parties;

drop table PLSQLTestTable;
