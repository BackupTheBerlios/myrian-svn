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
-- $Id: //core-platform/dev/sql/ccm-core/default/versioning/comment-vc_operations.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table vc_operations is '
  An operation is a single modification made to an attribute of
  an object by the user. Transactions are sets of operations.
';
comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';
