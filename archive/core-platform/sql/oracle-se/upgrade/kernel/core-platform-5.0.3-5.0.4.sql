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
-- Copyright (C) 2001 Red Hat Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/core-platform-5.0.3-5.0.4.sql#1 $

@@ group-member-map-constraints.sql
@@ permissions-5.0.3-5.0.4.sql
@@ ../../../../cms/content-section/sql/oracle-se/upgrade/cms-5.0.3-5.0.4.sql

-- Expressions service is no longer in use
drop table persistent_expressions;
