--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/update-applications.sql#4 $
-- $DateTime: 2004/03/30 17:47:27 $

-- In 6.0.1, the 'admin' and 'sitemap' package types already exist,
-- but they aren't application types.  Let's add the two missing rows
-- to the application_types table.

insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  acs_object_id_seq.nextval, 'com.arsdigita.ui.admin.Admin',
  'CCM Admin Application', 'CCM user and group administration',
  '1', '1', '0', '0', package_type_id
from apm_package_types
where package_key = 'admin';


insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  acs_object_id_seq.nextval, 'com.arsdigita.ui.sitemap.SiteMap',
  'SiteMap Admin Application', 'CCM sitemap administration',
  '1', '1', '0', '0', package_type_id
from apm_package_types
where package_key = 'sitemap';

-- Now that we have converted two package types into application
-- types, let's convert two existing package instances into
-- applications.

insert into acs_objects
  (object_id, object_type, display_name, default_domain_class)
values
  (acs_object_id_seq.nextval, 'com.arsdigita.ui.admin.Admin',
   'CCM Admin', 'com.arsdigita.ui.admin.Admin');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  acs_object_id_seq.currval,
  ap.pretty_name,
  at.application_type_id,
  currentDate(),
  sn.url,
  ap.package_id
from
  apm_packages ap,
  apm_package_types apt,
  application_types at,
  site_nodes sn
where
  ap.package_type_id = at.package_type_id
  and sn.object_id = ap.package_id
  and ap.package_type_id = apt.package_type_id
  and apt.package_key = 'admin';


insert into acs_objects
  (object_id, object_type, display_name, default_domain_class)
values
  (acs_object_id_seq.nextval, 'com.arsdigita.ui.sitemap.SiteMap',
   'CCM Admin Sitemap', 'com.arsdigita.ui.sitemap.SiteMap');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  acs_object_id_seq.currval,
  ap.pretty_name,
  at.application_type_id,
  currentDate(),
  sn.url,
  ap.package_id
from
  apm_packages ap,
  apm_package_types apt,
  application_types at,
  site_nodes sn
where
  ap.package_type_id = at.package_type_id
  and sn.object_id = ap.package_id
  and ap.package_type_id = apt.package_type_id
  and apt.package_key = 'sitemap';

insert into admin_app
select application_id
  from applications apps, application_types app_types
 where apps.application_type_id = app_types.application_type_id
   and app_types.object_type = 'com.arsdigita.ui.admin.Admin'
   and application_id not in (select application_id
                                from admin_app);
insert into sitemap_app
select application_id
  from applications apps, application_types app_types
 where apps.application_type_id = app_types.application_type_id
   and app_types.object_type = 'com.arsdigita.ui.sitemap.SiteMap'
   and application_id not in (select application_id
                                from sitemap_app);


