--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/categorization/index-cat_object_root_category_map.sql#4 $
-- $DateTime: 2004/04/07 16:07:11 $

create index cat_object_root_cat_object_idx on cat_object_root_category_map(object_id);
create index cat_object_root_cat_pkg_idx on cat_object_root_category_map(package_id);
create index cat_object_root_cat_rt_cat_idx on cat_object_root_category_map(root_category_id);
