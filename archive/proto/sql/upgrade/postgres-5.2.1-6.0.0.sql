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
-- $Id: //core-platform/proto/sql/upgrade/postgres-5.2.1-6.0.0.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $

begin;

\i ../default/upgrade/5.2.1-6.0.0/drop-search-test.sql
\i ../default/upgrade/5.2.1-6.0.0/sequence-vcx_id_seq.sql
\i ../default/upgrade/5.2.1-6.0.0/sequence-vcx_txns_id_seq.sql
\i ../default/upgrade/5.2.1-6.0.0/alter-categories.sql
\i ../default/upgrade/5.2.1-6.0.0/table-web_hosts-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/update-web-hosts.sql
\i ../postgres/upgrade/5.2.1-6.0.0/alter-lucene.sql
\i ../postgres/upgrade/5.2.1-6.0.0/mime-types.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_blob_operations-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_clob_operations-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_event_types-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_generic_operations-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_java_classes-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_obj_changes-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_operations-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_tags-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/table-vcx_txns-auto.sql
\i ../postgres/upgrade/5.2.1-6.0.0/vcx_deferred.sql
-- \i ../postgres/upgrade/5.2.1-6.0.0/update-constraints.sql
\i ../postgres/upgrade/5.2.1-6.0.0/misc.sql
\i ../default/upgrade/5.2.1-6.0.0/insert-vcx_event_types.sql
\i ../default/upgrade/5.2.1-6.0.0/insert-vcx_java_classes.sql
\i ../default/upgrade/5.2.1-6.0.0/add-index-cw_task_listeners_tid_ltid.sql

commit;

