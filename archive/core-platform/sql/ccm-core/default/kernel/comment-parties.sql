--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/comment-parties.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

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
