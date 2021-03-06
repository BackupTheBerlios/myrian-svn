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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/versioning/comment-vc_transactions.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

comment on table vc_transactions is '
  A transaction is a set of modifications that was made to an object''s
  attributes by a user during a database transaction. 
';
comment on column vc_transactions.master_id is '
  The ID of the top-level master object for this transaction
';
comment on column vc_transactions.object_id is '
  The ID of the object which was actually modified during the transaction
';
