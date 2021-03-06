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
-- $Id: //core-platform/test-packaging/sql/ccm-core/oracle-se/versioning/versioning-drop.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $


--
-- Drops tables neccesary for the Versioning object-level service
--
-- @author Joseph Bank (jbank@arsdigita.com)

drop table vc_blob_operations;
drop table vc_clob_operations;
drop table vc_generic_operations;
drop table vc_operations;
drop table vc_actions;
drop table vc_transactions;
drop table vc_objects;
