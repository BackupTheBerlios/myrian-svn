--
-- Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/upgrade/postgres-6.0.1-6.1.0.sql#1 $
-- $DateTime: 2004/01/15 18:48:26 $

\echo Red Hat WAF 6.0.1 -> 6.1.0 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/6.0.1-6.1.0/table-agentportlets-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-inits-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-init_requirements-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-keystore-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-lucene_ids-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/table-webapps-auto.sql
\i ../postgres/upgrade/6.0.1-6.1.0/deferred.sql

commit;
