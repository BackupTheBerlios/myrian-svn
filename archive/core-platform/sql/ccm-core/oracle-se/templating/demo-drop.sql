--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/templating/demo-drop.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $


-- Uninstall file for the data model created by 'demo-create.sql'
-- (This file created automatically by create-sql-uninst.pl.)
--
-- brech (Mon Aug 28 11:06:33 2000)
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/templating/demo-drop.sql#2 $
--

drop table ad_template_sample_users;
drop sequence ad_template_sample_users_seq;
