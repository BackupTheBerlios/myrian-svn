--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_context.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $
create or replace function upgrade_dnm_context ()
  returns integer as '
  declare
    c record;
  begin

    for c in select object_id from acs_objects where object_id != 0  loop
      perform dnm_context_add_object(c.object_id, 0);
    end loop;

    for c in select object_id, context_id 
               from object_context where object_id != 0 and context_id is not null and context_id != 0 loop
      perform dnm_context_change_context(c.object_id, c.context_id);
    end loop;

    for c in select object_id from acs_permissions loop
      perform dnm_context_add_grant(c.object_id);
    end loop;

    return null;
end; ' language 'plpgsql'
;

select upgrade_dnm_context();
