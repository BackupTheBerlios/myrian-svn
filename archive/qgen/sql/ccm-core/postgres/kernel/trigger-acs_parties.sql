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
-- $Id: //core-platform/test-qgen/sql/ccm-core/postgres/kernel/trigger-acs_parties.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


create or replace function parties_groups_in_fn () returns opaque as '
begin
  perform hierarchy_add_item(new.group_id, ''group_subgroup_trans_index'',
                             ''group_id'', ''subgroup_id'');
  return null;
end;' language 'plpgsql';

create trigger parties_groups_in_tr
after insert on groups
for each row execute procedure
parties_groups_in_fn();

-- Subgroup triggers


create or replace function parties_group_subgroup_in_fn () returns opaque as '
begin
  perform parties_add_subgroup(new.group_id, new.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_in_tr
after insert on group_subgroup_map
for each row execute procedure
parties_group_subgroup_in_fn();


create or replace function parties_group_subgroup_del_fn () returns opaque as '
begin
  perform parties_remove_subgroup(old.group_id, old.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_del_tr
after delete on group_subgroup_map
for each row execute procedure
parties_group_subgroup_del_fn();


create or replace function parties_group_subgroup_up_fn () returns opaque as '
begin
  perform parties_remove_subgroup(old.group_id, old.subgroup_id);
  perform parties_add_subgroup(new.group_id, new.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_up_tr
after update on group_subgroup_map
for each row execute procedure
parties_group_subgroup_up_fn();


-- Membership triggers

create or replace function parties_group_member_in_fn () returns opaque as '
begin
  perform parties_add_member(new.group_id, new.member_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_member_in_tr
after insert on group_member_map
for each row execute procedure
parties_group_member_in_fn();


create or replace function parties_group_member_del_fn () returns opaque as '
begin
  perform parties_remove_member(old.group_id, old.member_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_member_del_tr
after delete on group_member_map
for each row execute procedure
parties_group_member_del_fn();


create or replace function parties_group_member_up_fn () returns opaque as '
begin
  perform parties_remove_member(old.group_id, old.member_id);
  perform parties_add_member(new.group_id, new.member_id);
  return null;
end;' language 'plpgsql';

create trigger group_member_up_tr
after update on group_member_map
for each row execute procedure
parties_group_member_up_fn();
