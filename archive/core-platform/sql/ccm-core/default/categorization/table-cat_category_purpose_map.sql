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
-- $Id: //core-platform/dev/sql/ccm-core/default/categorization/table-cat_category_purpose_map.sql#2 $
-- $DateTime: 2003/11/17 15:52:45 $

create table cat_category_purpose_map (
  category_id             integer
                          constraint cat_cat_pur_map_cat_id_fk
                          references cat_categories,
  purpose_id              integer
                          constraint cat_obj_map_purpose_id_fk
                          references cat_purposes,
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id)
);
