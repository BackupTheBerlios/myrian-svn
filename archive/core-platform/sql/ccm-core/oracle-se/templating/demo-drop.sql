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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/templating/demo-drop.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $


-- Uninstall file for the data model created by 'demo-create.sql'
-- (This file created automatically by create-sql-uninst.pl.)
--
-- brech (Mon Aug 28 11:06:33 2000)
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/templating/demo-drop.sql#4 $
--

drop table ad_template_sample_users;
drop sequence ad_template_sample_users_seq;
