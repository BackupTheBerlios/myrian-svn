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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql#1 $
-- $DateTime: 2004/01/21 13:38:43 $
-- autor: Aram Kananov <aram@kananov.com>

create or replace function acs_object_dnm_ctx_add_fn () 
  returns trigger as '
  begin
      perform dnm_context_add_object(new.object_id,0);
      insert into object_context (object_id, context_id)
        values (new.object_id, null);
    return new;
  end; ' 
  language 'plpgsql'
;

create or replace function acs_object_dnm_ctx_del_fn () 
  returns trigger as '
  begin
      perform dnm_context_drop_object(old.object_id);
    return old;
  end; ' 
  language 'plpgsql'
;

create trigger acs_object_dnm_ctx_add_trg
  after insert  on acs_objects
  for each row 
  execute procedure acs_object_dnm_ctx_add_fn()
;

create trigger acs_object_dnm_ctx_del_trg
  before delete on acs_objects
  for each row 
  execute procedure acs_object_dnm_ctx_del_fn();
;

create or replace function object_context_dnm_fn ()
  returns trigger as '
  declare 
  begin
    if TG_OP = ''INSERT'' OR TG_OP = ''UPDATE'' THEN
      perform dnm_context_change_context(new.object_id, new.context_id);
      return new;
    ELSE 
      perform dnm_context_change_context(old.object_id, 0);
      return old;
    END IF;    
  end; ' language 'plpgsql'
;

create trigger object_context_dnm_trg
  after insert or update or delete 
  on object_context
  for each row 
  execute procedure object_context_dnm_fn();
;

create or replace function acs_permissions_dnm_ctx_fn ()
  returns trigger as '
  declare
  begin 
    if TG_OP = ''INSERT'' then
      perform dnm_context_add_grant(new.object_id);
    elsif TG_OP = ''DELETE'' then
      perform dnm_context_drop_grant(old.object_id);
    elsif new.object_id <> old.object_id then
      perform dnm_context_drop_grant(old.object_id);
      perform dnm_context_add_grant(new.object_id);
    end if;
    return null;
  end; ' language 'plpgsql'
;

create trigger acs_permissions_dnm_ctx_trg
  after insert or delete or update 
  on acs_permissions
  for each row 
  execute procedure acs_permissions_dnm_ctx_fn();
;
