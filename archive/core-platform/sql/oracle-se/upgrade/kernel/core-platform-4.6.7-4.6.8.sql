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
-- Data model upgrade from 4.6.7 to 4.6.8
--
-- Copyright (C) 2001 Arsdigita Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/core-platform-4.6.7-4.6.8.sql#1 $

-- PRE-UPGRADE
-- This script checks that the data is fit for upgrading (e.g., ensure that
-- columns that will be made non-nullable are in fact populated).
@@ core-platform-precheck-4.6.7-4.6.8.sql

WHENEVER SQLERROR EXIT ROLLBACK;

-- UPGRADE
-- During the upgrade, no errors should occur.  The pre-upgrade check above
-- should verify any preconditions of the upgrade.
@@ parties-4.6.7-4.6.8.sql

WHENEVER SQLERROR CONTINUE;

@@ ../../../../services/auditing/sql/oracle-se/upgrade/auditing-4.6.7-4.6.8.sql
@@ permissions-4.6.7-4.6.8.sql
@@ acs-stylesheet-4.6.7-4.6.8.sql
@@ apm-4.6.7-4.6.8.sql
@@ ../../../../services/categorization/sql/oracle-se/upgrade/categorization-4.6.7-4.6.8.sql
@@ ../../../../services/messaging/sql/oracle-se/upgrade/messaging-4.6.7-4.6.8.sql
@@ ../../../../services/notification/sql/oracle-se/upgrade/notification-4.6.7-4.6.8.sql
@@ ../../../../services/workflow/sql/oracle-se/upgrade/workflow-4.6.7-4.6.8.sql
@@ ../../../../infrastructure/globalization/sql/oracle-se/upgrade/g11n-4.6.7-4.6.8.sql
@@ ../../../../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.7-4.6.8.sql
