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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create or replace trigger acs_permission_dnm_pa_trg
  after insert or delete 
  on acs_permissions
  for each row
begin
  if inserting then
    dnm_parties.add_grant(:new.grantee_id);
  elsif deleting then
    dnm_parties.remove_grant(:old.grantee_id);
  end if;
end;
/
show errors

create or replace trigger group_subg_map_dnm_pa_trg
  after insert
  on group_subgroup_map
  for each row
begin
    dnm_parties.add_group_subgroup_map(:new.group_id, :new.subgroup_id);
end;
/
show errors 

  
create or replace trigger group_subg_tr_idx_dnm_pa_trg
  after delete
  on group_subgroup_trans_index
  for each row
begin
    dnm_parties.delete_group_member_map(:old.group_id, :old.subgroup_id);
end;
/
show errors

create or replace trigger group_mem_map_dnm_pa_trg
  after insert
  on group_member_map
  for each row
begin
    dnm_parties.add_group_user_map (:new.group_id, :new.member_id);
end;
/ 
show errors

create or replace trigger group_mem_tr_idx_dnm_pa_trg
  after delete
  on group_member_trans_index 
  for each row
begin
    dnm_parties.delete_group_member_map(:old.group_id, :old.member_id);
end;
/
show errors



create or replace trigger parties_dnm_pa_del_trg
  after delete
  on parties
  for each row
begin
    dnm_parties.delete_group_member_map(:old.party_id, :old.party_id);
end;
/
show errors
