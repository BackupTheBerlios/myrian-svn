--
-- Copyright (C) 2001 Red Hat Corporation. All Rights Reserved.
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
-- Copyright (C) 2001 Red Hat Corporation
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/kernel/core-platform-4.6.0-5.0.0.sql#1 $

@@ parties-4.6.2-4.6.3.sql
@@ objects-4.6.2-4.6.3.sql

drop table portlets;
drop table portlet_types;
drop table portals;

@@ ../notification/sql/oracle-se/upgrade/notification-4.6.2-4.6.3.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.2-4.6.3.sql

@@ parties-4.6.3-4.6.4.sql
@@ permissions-4.6.3-4.6.4.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.3-4.6.4.sql

@@ ../presentation/templating/sql/oracle-se/upgrade/stylesheet-upgrade-4.6.4-4.6.5.sql
@@ ../persistence/sql/oracle-se/upgrade/persistence-4.6.4-4.6.5.sql
@@ ../preferences/sql/oracle-se/upgrade/preferences-4.6.4-4.6.5.sql
@@ parties-4.6.4-4.6.5.sql
@@ apm-4.6.4-4.6.5.sql
@@ ../messaging/sql/oracle-se/upgrade/messaging-4.6.4-4.6.5.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.4-4.6.5.sql

@@ ../versioning/sql/oracle-se/upgrade/versioning-4.6.5-4.6.6.sql
@@ ../places/sql/oracle-se/upgrade/places-4.6.5-4.6.6.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.5-4.6.6.sql

@@ ../preferences/sql/oracle-se/upgrade/preferences-4.6.6-4.6.7.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.6-4.6.7.sql

@@ core-platform-precheck-4.6.7-4.6.8.sql

WHENEVER SQLERROR EXIT ROLLBACK;

-- UPGRADE
-- During the upgrade, no errors should occur.  The pre-upgrade check above
-- should verify any preconditions of the upgrade.
@@ parties-4.6.7-4.6.8.sql

WHENEVER SQLERROR CONTINUE;

@@ ../auditing/sql/oracle-se/upgrade/auditing-4.6.7-4.6.8.sql
@@ permissions-4.6.7-4.6.8.sql
@@ acs-stylesheet-4.6.7-4.6.8.sql
@@ apm-4.6.7-4.6.8.sql
@@ ../categorization/sql/oracle-se/upgrade/categorization-4.6.7-4.6.8.sql
@@ ../messaging/sql/oracle-se/upgrade/messaging-4.6.7-4.6.8.sql
@@ ../notification/sql/oracle-se/upgrade/notification-4.6.7-4.6.8.sql
@@ ../workflow/sql/oracle-se/upgrade/workflow-4.6.7-4.6.8.sql
@@ ../globalization/sql/oracle-se/upgrade/g11n-4.6.7-4.6.8.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.7-4.6.8.sql

@@ ../notification/sql/oracle-se/upgrade/notification-4.6.8-4.6.9.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.8-4.6.9.sql

@@ objects-4.6.9-4.7.1.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.6.9-4.7.1.sql
@@ ../persistence/sql/oracle-se/upgrade/persistence-4.6.9-4.7.1.sql

@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.7.1-4.8.1.sql

@@ permissions-4.8.1-4.8.2.sql
@@ ../search/sql/oracle-se/upgrade/upgrade-search-4.8.1-4.8.2.sql
@@ ../categorization/sql/oracle-se/upgrade/categorization-4.8.1-4.8.2.sql
@@ ../admin/sql/oracle-se/upgrade/upgrade-4.8.1-4.8.2.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.8.1-4.8.2.sql

@@ parties-4.8.2-4.8.3.sql
@@ permissions-4.8.2-4.8.3.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.8.2-4.8.3.sql

@@ parties-4.8.4-4.8.5.sql
@@ ../formbuilder/sql/oracle-se/upgrade/upgrade-4.8.4-4.8.5.sql
@@ ../messaging/sql/oracle-se/upgrade/messaging-4.8.4-4.8.5.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.8.4-4.8.5.sql

@@ objects-4.8.5-4.8.6.sql
@@ ../messaging/sql/oracle-se/upgrade/messaging-4.8.5-4.8.6.sql

@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.8.6-4.8.7.sql

@@ ../cms/content-section/sql/oracle-se/upgrade/upgrade-4.8.8-4.8.9.sql

@@ permissions-4.8.10-4.8.11.sql
@@ acs-stylesheet-4.8.10-4.8.11.sql
@@ ../notification/sql/oracle-se/upgrade/notification-4.8.10-4.8.11.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/cms-4.8.10-4.8.11.sql

@@ permissions-4.8.11-5.0.1.sql
@@ ../categorization/sql/oracle-se/upgrade/categorization-4.8.11-5.0.1.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/cms-4.8.11-5.0.1.sql

@@ ../cms/content-section/sql/oracle-se/upgrade/cms-5.0.1-5.0.2.sql

@@ ../cms/content-section/sql/oracle-se/upgrade/cms-5.0.2-5.0.3.sql

@@ group-member-map-constraints.sql
@@ permissions-5.0.3-5.0.4.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/cms-5.0.3-5.0.4.sql

-- Expressions service is no longer in use
drop table persistent_expressions;

@@ ../versioning/sql/oracle-se/upgrade/versioning-5.0.4-5.0.5.sql
@@ ../cms/content-section/sql/oracle-se/upgrade/cms-5.0.4-5.0.5.sql
