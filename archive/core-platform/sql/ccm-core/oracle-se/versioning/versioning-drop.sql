--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/versioning/versioning-drop.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- Drops tables neccesary for the Versioning object-level service
--
-- @author Joseph Bank (jbank@arsdigita.com)

drop table vc_blob_operations;
drop table vc_clob_operations;
drop table vc_generic_operations;
drop table vc_operations;
drop table vc_actions;
drop table vc_transactions;
drop table vc_objects;
