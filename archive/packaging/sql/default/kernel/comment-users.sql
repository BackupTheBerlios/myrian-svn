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
-- $Id: //core-platform/test-packaging/sql/default/kernel/comment-users.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

comment on table users is '
 A user is a type of party.  In the data object model and domain object layer,
 we need to make sure that a user has a unique primary email address that is
 not null.  The primary email address referenced in the parties table is
 non unique and nullable.
';
