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
-- $Id: //core-platform/dev/sql/oracle-se/categorization/categorization-drop.sql#4 $
-- $DateTime: 2003/08/15 13:46:34 $


--
-- //enterprise/services/dev/categorization/sql/categorization-drop.sql
--
-- @author randyg@arsdigita.com
-- @creation-date 2001-05-10
-- @cvs-id $Id: //core-platform/dev/sql/oracle-se/categorization/categorization-drop.sql#4 $
--

drop index cat_cat_pur_map_category_id_idx;
drop table cat_category_purpose_map;
drop table cat_purposes;
drop table cat_object_root_category_map;
drop table cat_object_category_map;
drop table cat_category_category_map;
drop table cat_categories;
delete from acs_objects where object_type like 'com.arsdigita.categorization%';
