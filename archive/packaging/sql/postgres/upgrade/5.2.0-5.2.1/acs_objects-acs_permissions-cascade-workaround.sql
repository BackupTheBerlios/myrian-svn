-- This trigger manually cascades deletes between acs_objects and
-- acs_permissions. This works around a bug in postgres that causes
-- referential integrity checks to be performed too early.

create or replace function acs_permissions_cascade_del_fn()
returns opaque as '
begin
    delete from acs_permissions where object_id = old.object_id;
    return old;
end;' language 'plpgsql';

create trigger acs_permissions_cascade_del_tr
before delete on acs_objects
for each row execute procedure acs_permissions_cascade_del_fn();
