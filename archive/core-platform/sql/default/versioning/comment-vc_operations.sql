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
-- $Id: //core-platform/dev/sql/default/versioning/comment-vc_operations.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

comment on table vc_operations is '
  An operation is a single modification made to an attribute of
  an object by the user. Transactions are sets of operations.
';
comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';
