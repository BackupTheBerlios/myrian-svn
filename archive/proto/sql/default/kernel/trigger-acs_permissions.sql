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
-- $Id: //core-platform/proto/sql/default/kernel/trigger-acs_permissions.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $


create or replace trigger object_context_in_tr
after insert on object_context
for each row
begin
  if :new.context_id is not null then
    permission_denormalization.add_context(:new.object_id, :new.context_id);
  end if;
end;
/
show errors

create or replace trigger object_context_up_tr
after update on object_context
for each row
begin
  if ((:old.context_id != :new.context_id) or
      (:old.context_id is null) or
      (:new.context_id is null)) then

    if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
    end if;
    if :new.context_id is not null then
      permission_denormalization.add_context(:new.object_id, :new.context_id);
    end if;

  end if;
end;
/
show errors

create or replace trigger object_context_del_tr
before delete on object_context
for each row
begin
  if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
  end if;
end;
/
show errors

create or replace trigger acs_objects_context_in_tr
after insert on acs_objects
for each row
begin
   insert into object_context
   (object_id, context_id)
   values
   (:new.object_id, null);
end;
/
show errors

--
-- Triggers on acs_permissions to maintain above denormalizations
--

create or replace trigger acs_permissions_in_tr
after insert on acs_permissions
for each row
begin
    permission_denormalization.add_grant(:new.object_id);
end;
/
show errors

-- this trigger supports a fringe case where someone updates a
-- a grant (i.e. row in acs_permissions) and chagnes the object_id.
create or replace trigger acs_permissions_up_tr
after update on acs_permissions
for each row
begin
    if (:old.object_id != :new.object_id) then
        permission_denormalization.remove_grant(:old.object_id);
        permission_denormalization.add_grant(:new.object_id);
    end if;
end;
/
show errors

create or replace trigger acs_permissions_del_tr
after delete on acs_permissions
for each row
begin
    permission_denormalization.remove_grant(:old.object_id);
end;
/
show errors
