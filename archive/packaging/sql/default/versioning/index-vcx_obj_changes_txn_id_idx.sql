--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-packaging/sql/default/versioning/index-vcx_obj_changes_txn_id_idx.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

-- index foreign keys
create index vcx_obj_changes_txn_id_idx on vcx_obj_changes(txn_id);
