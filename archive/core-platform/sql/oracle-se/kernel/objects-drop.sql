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
-- $Id: //core-platform/dev/sql/oracle-se/kernel/objects-drop.sql#4 $
-- $DateTime: 2003/06/13 20:03:18 $


--
-- //enterprise/kernel/dev/kernel/sql/objects-drop.sql 
--
-- @author oumi@arsdigita.com
-- @creation-date 2001-05-10
-- @cvs-id $Id: //core-platform/dev/sql/oracle-se/kernel/objects-drop.sql#4 $
--

drop table object_container_map;

drop table acs_objects;

drop sequence acs_object_id_seq;
drop sequence vcx_txns_id_seq;
drop sequence vcx_id_seq;
