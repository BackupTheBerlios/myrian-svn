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
-- $Id: //core-platform/proto/sql/default/kernel/index-apm_packages.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

create index apm_packages_locale_id_idx on apm_packages(locale_id);
create index apm_packages_package_type_idx on apm_packages(package_type_id);
