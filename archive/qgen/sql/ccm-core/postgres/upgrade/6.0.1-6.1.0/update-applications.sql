-- Since: 2004-03-23

-- In 6.0.1, the 'admin' and 'sitemap' package types already exist,
-- but they aren't application types.  Let's add the two missing rows
-- to the application_types table.

insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  nextval('acs_object_id_seq'), 'com.arsdigita.ui.admin.Admin',
  'CCM Admin Application', 'CCM user and group administration',
  't', 't', 'f', 'f', package_type_id
from apm_package_types
where package_key = 'admin';


insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  nextval('acs_object_id_seq'), 'com.arsdigita.ui.sitemap.SiteMap',
  'SiteMap Admin Application', 'CCM sitemap administration',
  't', 't', 'f', 'f', package_type_id
from apm_package_types
where package_key = 'sitemap';

-- Now that we have converted two package types into application
-- types, let's convert two existing package instances into
-- applications.

insert into acs_objects
  (object_id, object_type, display_name, default_domain_class)
values
  (nextval('acs_object_id_seq'), 'com.arsdigita.ui.admin.Admin',
   'CCM Admin', 'com.arsdigita.ui.admin.Admin');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  currval('acs_object_id_seq'),
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
  (nextval('acs_object_id_seq'), 'com.arsdigita.ui.sitemap.SiteMap',
   'CCM Admin Sitemap', 'com.arsdigita.ui.sitemap.SiteMap');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  currval('acs_object_id_seq'),
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
