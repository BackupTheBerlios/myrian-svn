--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/5.2.1-6.0.0/alter-categories.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

alter table cat_object_category_map add  index_p char(1)
                          constraint cat_obj_map_index_p_ck
                          check(index_p in ('0','1'));
