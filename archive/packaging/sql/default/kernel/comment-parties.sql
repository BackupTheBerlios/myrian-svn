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
-- $Id: //core-platform/test-packaging/sql/default/kernel/comment-parties.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

comment on table parties is '
 Party is the supertype of user and group. It exists because
 many other types of object can have relationships to parties.
';
comment on column parties.primary_email is '
 Stores a reference to the party''s primary email address.
';
comment on column parties.uri is '
 This URI is a user-specified URI for the party.  E.g., a personal web page,
 a company web site, etc.
';
