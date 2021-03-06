--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/comment-parties.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
