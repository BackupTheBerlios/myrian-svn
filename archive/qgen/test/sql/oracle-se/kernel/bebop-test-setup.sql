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
-- $Id: //core-platform/test-qgen/test/sql/oracle-se/kernel/bebop-test-setup.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


declare 
 node_id    number;
 package_id number;
 main_site  number;
 version_id number;
begin
    apm_package_type.create_type (
     PACKAGE_KEY => 'bebop_test',
     PRETTY_NAME => 'Bebop Test Package',
     PRETTY_PLURAL => 'Bebop Test Packages',
     PACKAGE_URI =>  'http://arsdigita.com',
     PACKAGE_TYPE => 'apm_application',
     SINGLETON_P => 'f'
    );

    package_id := apm_package.new (package_key => 'bebop_test');

    apm_package.enable (package_id => package_id);
   
    select node_id into main_site from site_nodes
      where parent_id is null;

    node_id := site_node.new(
       name => 'bebop-test',
       parent_id => main_site,
       directory_p => 't',
       pattern_p => 't',
       object_id => package_id
    );

    version_id := apm_package_version.new(
     package_key => 'bebop_test',
     version_uri => 'http://',
     version_name => '0.1d',
     summary => 'bebop test package', 
     description_format => 'text/plain',
     description => 'bebop test package', 
     release_date => sysdate,
     vendor => 'arsdigita',
     vendor_uri => 'arsdigita.com', 
     dispatcher_class => 'com.arsdigita.bebop.demo.BebopTestDispatcher'
    );

    apm_package_version.enable (version_id => version_id);

end;
/
show errors
