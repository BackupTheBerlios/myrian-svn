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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/categorization/categorization-4.8.1-4.8.2.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


-- Upgrades the data model from version 4.8.1 - 4.8.2
--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/categorization/categorization-4.8.1-4.8.2.sql#3 $

-- foreign key index. Also needed to significantly reduce io when
-- looking for categories for an object
create index cat_obj_cat_map_object_id_idx on cat_object_category_map(object_id);
