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
-- $Id: //core-platform/dev/sql/default/versioning/comment-vc_objects.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

comment on table vc_objects is '
  Tracks all the objects that are versioned.
';
comment on column vc_objects.is_deleted is '
  If true (1), the object has been deleted and cannot be successfully
  retrieved by normal means.
';
comment on column vc_objects.master_id is '
  The master object for this versioned object; that is, the
  very top-level object of which this object is a composite.
  Used by the versioning system to keep track of transactions
  for an object
';

