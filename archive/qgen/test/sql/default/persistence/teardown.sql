--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-qgen/test/sql/default/persistence/teardown.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
-- @version $Revision: #1 $ $Date: 2003/12/10 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.

drop table PLSQLTestTable;
@@ ../../default/persistence/aggressive-teardown.sql
