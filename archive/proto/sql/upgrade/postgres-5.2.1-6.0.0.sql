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
-- $Id: //core-platform/proto/sql/upgrade/postgres-5.2.1-6.0.0.sql#1 $
-- $DateTime: 2003/05/08 15:13:21 $


begin;

\i ../postgres/lucene/proc-update-dirty.sql

drop trigger acs_permissions_cascade_del_tr on acs_objects;
drop function acs_permissions_cascade_del_fn();

commit;

