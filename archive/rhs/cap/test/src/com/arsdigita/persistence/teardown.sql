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
-- $Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/teardown.sql#1 $
-- $DateTime: 2004/05/05 16:10:50 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
-- @version $Revision: #1 $ $Date: 2004/05/05 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.

drop table PLSQLTestTable;
@@ aggressive-teardown.sql
