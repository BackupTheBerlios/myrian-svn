--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/setup.pg.sql#2 $
-- $DateTime: 2004/05/25 17:44:35 $


--
-- This file sets up all the data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2004/05/25 $
--

create view dual as select 1;

@@ data-operation-test.sql
@@ aggressive-test.sql
@@ data-query-test.sql
@@ sql-operation-test.sql
@@ data-operation-test.sql
