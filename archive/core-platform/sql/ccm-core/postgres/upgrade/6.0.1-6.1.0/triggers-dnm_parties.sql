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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql#1 $
-- $DateTime: 2004/01/21 13:38:43 $
-- autor: Aram Kananov <aram@kananov.com>


create or replace function acs_permissions_dnm_parties_fn ()
  returns trigger as '
  declare 
  begin
    if TG_OP = ''INSERT'' then
      perform dnm_parties_add_grant(new.grantee_id);
      return new;
    elsif TG_OP = ''DELETE'' then
      perform dnm_parties_remove_grant(old.grantee_id);
      return new;
    end if;
  end; ' language 'plpgsql'
;

create trigger acs_permissions_dnm_parties_trg 
  after insert or delete 
  on acs_permissions 
  for each row
  execute procedure acs_permissions_dnm_parties_fn()
;


create or replace function group_subgroup_map_dnm_parties_fn ()
  returns trigger as '
  declare
  begin
    perform dnm_parties_add_group_subgroup_map(new.group_id, new.subgroup_id);
    return null;
  end; ' language 'plpgsql'
;

create trigger group_subgroup_map_dnm_parties_trg
  after insert
  on group_subgroup_map 
  for each row
  execute procedure group_subgroup_map_dnm_parties_fn()
;


create or replace function group_subgr_tr_idx_dnm_parties_fn ()
  returns trigger as '
  begin 
    perform  dnm_parties_delete_map(old.group_id, old.subgroup_id); 
    return null;
  end; ' language 'plpgsql'
;

create trigger group_subgr_tr_idx_dnm_parties_trg
  after delete 
  on group_subgroup_trans_index
  for each row
  execute procedure group_subgr_tr_idx_dnm_parties_fn()
;


create or replace function group_member_map_dnm_parties_fn ()
  returns trigger as '
  declare
  begin
    perform dnm_parties_add_group_user_map(new.group_id, new.member_id);
    return null;
  end; ' language 'plpgsql'
;

create trigger group_member_map_dnm_parties_trg
  after insert 
  on group_member_map
  for each row
  execute procedure group_member_map_dnm_parties_fn()
;

create or replace function group_member_trans_idx_dnm_parties_fn()
  returns trigger as '
  declare
  begin
     perform dnm_parties_delete_map(old.group_id, old.member_id);
     return null;
  end; ' language 'plpgsql'
;

create trigger group_member_trans_idx_dnm_parties_trg
  after delete 
  on group_member_trans_index 
  for each row
  execute procedure group_member_trans_idx_dnm_parties_fn ()
;

create or replace function parties_dnm_paries_fn() 
  returns trigger as '
  declare
  begin
    perform dnm_parties_delete_map(old.party_id, old.party_id);
    return null;
  end; ' language 'plpgsql'
;
  
create trigger parties_dnm_paries_trg
  after delete
   on parties
  for each row
  execute procedure parties_dnm_paries_fn()
;
