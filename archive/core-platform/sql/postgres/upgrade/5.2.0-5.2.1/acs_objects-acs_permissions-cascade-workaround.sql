--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/postgres/upgrade/5.2.0-5.2.1/acs_objects-acs_permissions-cascade-workaround.sql#2 $
-- $DateTime: 2003/08/15 13:46:34 $


create or replace function acs_permissions_cascade_del_fn()
returns opaque as '
begin
    delete from acs_permissions where object_id = old.object_id;
    return old;
end;' language 'plpgsql';

create trigger acs_permissions_cascade_del_tr
before delete on acs_objects
for each row execute procedure acs_permissions_cascade_del_fn();
