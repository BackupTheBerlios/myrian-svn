--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/upgrade/postgres-6.0.0-6.0.1.sql#1 $
-- $DateTime: 2004/03/11 12:01:18 $

\echo Red Hat WAF 6.0.0 -> 6.0.1 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/kernel/package-parties_denormalization.sql

commit;
