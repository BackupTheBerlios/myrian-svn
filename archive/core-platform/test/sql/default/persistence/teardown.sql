--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/test/sql/default/persistence/teardown.sql#12 $
-- $DateTime: 2004/03/30 17:47:27 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
-- @version $Revision: #12 $ $Date: 2004/03/30 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.

drop table PLSQLTestTable;
@@ ../../default/persistence/aggressive-teardown.sql
