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
-- $Id: //core-platform/dev/sql/default/kernel/trigger-acs_parties.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $


create or replace trigger groups_in_tr
after insert on groups
for each row
begin
    parties_denormalization.add_group(:new.group_id);
end;
/
show errors

-- Subgroup triggers

create or replace trigger group_subgroup_in_tr
after insert on group_subgroup_map
for each row
begin
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_del_tr
after delete on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_up_tr
after update on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

-- Membership triggers

create or replace trigger group_member_in_tr
after insert on group_member_map
for each row
begin
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors

create or replace trigger group_member_del_tr
after delete on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
end;
/
show errors

create or replace trigger group_member_up_tr
after update on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors
