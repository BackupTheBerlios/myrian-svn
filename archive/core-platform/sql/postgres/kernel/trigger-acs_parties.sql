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
