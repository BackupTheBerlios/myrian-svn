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
-- $Id: //core-platform/proto/sql/default/x/versioning/comment-vcx_obj_changes.sql#1 $
-- $DateTime: 2003/04/09 16:35:55 $

comment on table vcx_obj_changes is '
  A change is a set of modifications that was made to an object''s
  attributes by a user during a database change. 
';
comment on column vcx_obj_changes.master_id is '
  The ID of the top-level master object for this change
';
comment on column vcx_obj_changes.object_id is '
  The ID of the object which was actually modified during the change
';
