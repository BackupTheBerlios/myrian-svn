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
-- $Id: //core-platform/dev/sql/ccm-core/default/categorization/table-cat_object_root_category_map.sql#2 $
-- $DateTime: 2003/11/17 15:52:45 $

create table cat_object_root_category_map (
       root_category_id           integer
                                 constraint cat_obj_root_map_fk
                                 references cat_categories on delete cascade,
       package_id                integer
                                 constraint cat_obj_package_id_fk
                                 references apm_packages 
                                 on delete cascade,
       -- most of the time the object_id is actually going to
       -- be a user_id and used for personalizing categories
       -- hierarchies within a package
       object_id                 integer
                                 constraint cat_obj_object_id_fk
                                 references acs_objects on delete cascade,
       -- this is used to allow for package type mappings
       -- e.g. if every bboard wants to have the same category
       --  then leave package_id and object_id null and set
       --  object_type to 'bboard'
       object_type               varchar(100)
);
