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
-- $Id: //core-platform/test-packaging/sql/postgres/kernel/trigger-acs_permissions.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $


create or replace function object_context_in_fn () returns opaque as '
begin
  if new.context_id is not null then
    perform permissions_add_context(new.object_id, new.context_id);
  end if;
  return null;
end;' language 'plpgsql';

create trigger object_context_in_tr
after insert on object_context
for each row execute procedure object_context_in_fn();


create or replace function object_context_up_fn () returns opaque as '
begin
  if ((old.context_id != new.context_id) or
      (old.context_id is null) or
      (new.context_id is null)) then

    if old.context_id is not null then
      perform permissions_remove_context(old.object_id, 
                                         old.context_id);
    end if;
    if new.context_id is not null then
      perform permissions_add_context(new.object_id, new.context_id);
    end if;

  end if;
  return null;
end;' language 'plpgsql';

create trigger object_context_up_tr
after update on object_context
for each row execute procedure object_context_up_fn();


create or replace function object_context_del_fn () returns opaque as '
begin
  if old.context_id is not null then
       perform permissions_remove_context(old.object_id, 
                                          old.context_id);
  end if;
  return old;
end;' language 'plpgsql';

create trigger object_context_del_tr
before delete on object_context
for each row execute procedure object_context_del_fn();


create or replace function acs_objects_context_in_fn () returns opaque as '
begin
   insert into object_context
   (object_id, context_id)
   values
   (new.object_id, null);
   return null;
end;' language 'plpgsql';

create trigger acs_objects_context_in_tr
after insert on acs_objects
for each row execute procedure acs_objects_context_in_fn();

--
-- Triggers on acs_permissions to maintain above denormalizations
--




create or replace function acs_permissions_in_fn () returns opaque as '
begin
  perform permissions_add_grant(new.object_id);
  return null;
end;' language 'plpgsql';


create trigger acs_permissions_in_tr
after insert on acs_permissions
for each row execute procedure acs_permissions_in_fn();


create or replace function acs_permissions_up_fn () returns opaque as '
begin
    if (old.object_id != new.object_id) then
        perform permissions_remove_grant(old.object_id);
        perform permissions_add_grant(new.object_id);
    end if;
    return null;
end;' language 'plpgsql';

-- this trigger supports a fringe case where someone updates a
-- a grant (i.e. row in acs_permissions) and chagnes the object_id.
create trigger acs_permissions_up_tr
after update on acs_permissions
for each row  execute procedure acs_permissions_up_fn();



create or replace function acs_permissions_del_fn () returns opaque as '
begin
    perform permissions_remove_grant(old.object_id);
    return null;
end;' language 'plpgsql';

create trigger acs_permissions_del_tr
after delete on acs_permissions
for each row execute procedure acs_permissions_del_fn();
