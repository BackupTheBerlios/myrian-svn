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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql#1 $
-- $DateTime: 2004/01/21 12:36:48 $
-- autor: Aram Kananov <aram@kananov.com>

create or replace trigger acs_permissions_dnm_ctx_trg
  after insert or update or delete
  on acs_permissions
  for each row
begin
  if inserting then
     dnm_context.add_grant(:new.object_id);
  elsif deleting then
     dnm_context.remove_grant(:old.object_id);
  elsif updating then
     dnm_context.remove_grant(:old.object_id);
     dnm_context.add_grant(:new.object_id);
  end if;
end;
/
show errors

create or replace trigger object_context_dnm_ctx_trg
  after insert or update or delete
  on object_context
  for each row
begin
  if inserting or updating then
     dnm_context.change_context(:new.object_id, :new.context_id);
   else
     dnm_context.change_context(:old.object_id, null);
   end if;
end;
/
show errors

create or replace trigger acs_objects_dnm_ctx_in_trg
  before insert
  on acs_objects
  for each row
begin
     dnm_context.add_object(:new.object_id,null);
end;
/

create or replace trigger acs_objects_dnm_ctx_aftin_trg
  after insert
  on acs_objects
  for each row
begin
      insert into object_context (object_id, context_id)
        values (:new.object_id, null);
end;
/


create or replace trigger acs_objects_dnm_ctx_del_trg
  after delete
  on acs_objects
  for each row
begin
     dnm_context.drop_object(:old.object_id);
end;
/
