--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- Remove user and group site nodes, APM Packages and stylesheets.
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/admin/upgrade-4.8.1-4.8.2.sql#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $

delete from acs_stylesheet_type_map 
 where stylesheet_id in (select stylesheet_id 
                         from acs_stylesheets
                         where pathname in ('/packages/acs-admin/xsl/group_en.xsl',
                                            '/packages/acs-admin/xsl/user_en.xsl')); 
delete from acs_stylesheets
 where  pathname in ('/packages/acs-admin/xsl/group_en.xsl',
                     '/packages/acs-admin/xsl/user_en.xsl'); 

delete from acs_objects
 where object_type='com.arsdigita.kernel.Stylesheet'
   and display_name in ('/packages/acs-admin/xsl/user_en.xsl',
                       '/packages/acs-admin/xsl/group_en.xsl');

delete from site_nodes
 where name in ('groups', 'users');

delete from acs_objects
 where object_type='com.arsdigita.kernel.SiteNode'
   and display_name in ('/admin/users/', '/admin/groups/');

delete from apm_packages
 where pretty_name in ('ACS Admin Groups',
                       'ACS Admin Users');

delete from acs_objects
 where object_type='com.arsdigita.kernel.PackageInstance'
   and display_name in ('ACS Admin Groups', 'ACS Admin Users');

delete from apm_package_types
 where package_key in ('groups', 'users');

update acs_stylesheets
   set locale_id = (select locale_id from g11n_locales where language='en' and country is null),
       pathname = '/packages/acs-admin/xsl/admin_en.xsl'
 where pathname='/packages/acs-admin/xsl/admin.xsl';
