--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_context.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $
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
