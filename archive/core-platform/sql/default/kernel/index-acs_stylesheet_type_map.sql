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
-- $Id: //core-platform/dev/sql/default/kernel/index-acs_stylesheet_type_map.sql#3 $
-- $DateTime: 2003/08/15 13:46:34 $

create index acs_stylesheet_type_pkg_idx on acs_stylesheet_type_map(stylesheet_id);
create index acs_stylesheet_type_sheet_idx on acs_stylesheet_type_map(package_type_id);
