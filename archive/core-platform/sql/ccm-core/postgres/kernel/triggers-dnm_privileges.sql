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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/kernel/triggers-dnm_privileges.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create or replace function acs_privileges_dnm_pr_fn()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' THEN 
      perform dnm_privileges_add_privilege(new.privilege);
      return new;
    elsif TG_OP = ''DELETE'' THEN 
      perform dnm_privileges_delete_privilege(old.privilege);
      return old;
    elsif TG_OP = ''UPDATE'' THEN
      perform dnm_privileges_delete_privilege(old.privilege);
      perform dnm_privileges_add_privilege(new.privilege);
      return new;
    end if; 
  end; ' language 'plpgsql'
;


create trigger acs_privileges_dnm_pr_trg
  after insert or delete or update
  on acs_privileges
  for each row
  execute procedure acs_privileges_dnm_pr_fn()
;


create or replace function acs_priv_hier_dnm_pr_fn ()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' THEN
      perform dnm_privileges_map_add_child_priv(new.privilege, new.child_privilege);
      return new;
    elsif TG_OP = ''DELETE'' THEN
      perform dnm_privileges_delete_child_privilege(old.privilege, old.child_privilege);
      return old;
    elsif TG_OP = ''UPDATE'' THEN
      perform dnm_privileges_delete_child_privilege(old.privilege, old.child_privilege);
      perform dnm_privileges_map_add_child_priv(new.privilege, new.child_privilege);
      return new;
    end if;
  end; ' language 'plpgsql'
;

create trigger acs_priv_hier_dnm_pr_trg
  after insert or delete
  on acs_privilege_hierarchy
  for each row
  execute procedure acs_priv_hier_dnm_pr_fn()
;


create or replace function acs_permission_dnm_pr_fn()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' then
      perform dnm_privileges_add_grant(new.object_id, new.grantee_id, new.privilege);  
      return new;
    elsif TG_OP = ''DELETE'' then
      perform dnm_privileges_remove_grant(old.object_id, old.grantee_id, old.privilege);
      return old;
    end if;
end; ' language 'plpgsql'
;
      

create trigger acs_permission_dnm_pr_trg
  after insert or delete 
  on acs_permissions
  for each row
  execute procedure acs_permission_dnm_pr_fn()
;

