--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/upgrade/postgres-5.2.0-5.2.1.sql#1 $
-- $DateTime: 2003/05/01 17:50:37 $


begin;
\i ../postgres/upgrade/5.2.0-5.2.1/alter-requests.sql
\i ../postgres/upgrade/5.2.0-5.2.1/add-cat-deflt-ancestors-idx.sql
\i ../postgres/upgrade/5.2.0-5.2.1/acs_objects-acs_permissions-cascade-workaround.sql

\i ../default/notification/index-nt_requests.sql

commit;

--

