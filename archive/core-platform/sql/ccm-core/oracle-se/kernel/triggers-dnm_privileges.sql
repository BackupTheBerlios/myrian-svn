--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/kernel/triggers-dnm_privileges.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create or replace trigger acs_privileges_dnm_pr_trg
  after insert or delete
  on acs_privileges
  for each row
begin
  if inserting then
    dnm_priv_utils.add_privilege(:new.privilege);
  elsif deleting then
    dnm_priv_utils.delete_privilege(:old.privilege);
  end if;
end;
/
show errors

create or replace trigger acs_priv_hier_dnm_pr_trg
  after insert or delete
  on acs_privilege_hierarchy
  for each row
begin
  if inserting then
    dnm_priv_utils.add_child_privilege(:new.privilege,:new.child_privilege);
  elsif deleting then
    dnm_priv_utils.delete_child_privilege(:old.privilege, :old.child_privilege);
  end if;
end;
/
show errors

create or replace trigger acs_permission_dnm_pr_trg
  after insert or delete 
  on acs_permissions
  for each row
begin
  if inserting then
    dnm_priv_utils.add_grant(:new.object_id, :new.grantee_id, :new.privilege);
  elsif deleting then
    dnm_priv_utils.remove_grant(:old.object_id, :old.grantee_id, :old.privilege);
  end if;
end;
/

