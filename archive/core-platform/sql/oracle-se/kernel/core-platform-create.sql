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
-- /enterprise/kernel/dev/kernel/sql/acs-4.5-create.sql
-- 
-- A creation script for ACS 4.5   Loads minimal datamodel. 
--
-- @author Bryan Quinn (bquinn@arsdigita.com) 
-- @creation-date May 16, 2001 20:16:43
-- @version $Id: //core-platform/dev/sql/oracle-se/kernel/core-platform-create.sql#3 $
--

@@ ../globalization/g11n-create.sql
@@ ../preferences/preferences-create.sql
@@ objects-create.sql
@@ parties-create.sql
@@ security-create.sql
@@ permissions-create.sql
@@ site-nodes-create.sql
@@ apm-create.sql
@@ acs-stylesheet-create.sql
@@ notes-create.sql
@@ ../categorization/categorization-create.sql
@@ ../auditing/auditing-create.sql
@@ ../messaging/messaging-create.sql
@@ ../notification/notification-create.sql
@@ ../search/search-create.sql
@@ ../places/places-create.sql
@@ ../versioning/versioning-create.sql
@@ ../workflow/workflow-create.sql
@@ ../formbuilder/formbuilder-create.sql
@@ ../addresses/us-addresses-create.sql
@@ ../persistence/persistence-create.sql
