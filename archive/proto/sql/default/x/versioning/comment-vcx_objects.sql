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
-- $Id: //core-platform/proto/sql/default/x/versioning/comment-vcx_objects.sql#1 $
-- $DateTime: 2003/04/09 16:35:55 $

comment on table vcx_objects is '
  Tracks all the objects that are versioned.
';
comment on column vcx_objects.is_deleted is '
  If true (1), the object has been deleted and cannot be successfully
  retrieved by normal means.
';
comment on column vcx_objects.master_id is '
  The master object for this versioned object; that is, the
  very top-level object of which this object is a composite.
  Used by the versioning system to keep track of transactions
  for an object
';

