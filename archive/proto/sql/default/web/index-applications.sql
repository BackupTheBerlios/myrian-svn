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
-- $Id: //core-platform/proto/sql/default/web/index-applications.sql#1 $
-- $DateTime: 2003/04/09 16:35:55 $

create index applicati_applicati_typ_id_idx on applications(application_type_id);
create index applicati_package_id_idx on applications(package_id);
create index applicati_parent_app_id_idx on applications(parent_application_id);
