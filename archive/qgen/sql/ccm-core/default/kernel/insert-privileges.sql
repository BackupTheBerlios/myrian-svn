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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/kernel/insert-privileges.sql#2 $
-- $DateTime: 2004/01/29 12:35:08 $


insert into acs_privileges (privilege) values ('read');
insert into acs_privileges (privilege) values ('create');
insert into acs_privileges (privilege) values ('write');
insert into acs_privileges (privilege) values ('delete');
insert into acs_privileges (privilege) values ('admin');
insert into acs_privileges (privilege) values ('edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('create', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('delete', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('edit', 'admin');



-- CMS Privileges 
insert into acs_privileges (privilege) values ('cms_staff_admin');
insert into acs_privileges (privilege) values ('cms_category_admin');
insert into acs_privileges (privilege) values ('cms_publish');
insert into acs_privileges (privilege) values ('cms_new_item');
insert into acs_privileges (privilege) values ('cms_edit_item');
insert into acs_privileges (privilege) values ('cms_delete_item');
insert into acs_privileges (privilege) values ('cms_read_item');
insert into acs_privileges (privilege) values ('cms_preview_item');
insert into acs_privileges (privilege) values ('cms_categorize_items');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_staff_admin','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_category_admin','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_publish','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_new_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items','admin');


-- This previously was implied by 
--    c.a.kernel.pemissions.PermissionManager.s_implications

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_preview_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_delete_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_delete_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_staff_admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_category_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_edit_item');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_new_item');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_publish', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_new_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_category_admin', 'cms_staff_admin');
