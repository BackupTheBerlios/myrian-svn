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
-- $Id: //core-platform/test-packaging/sql/postgres/upgrade/5.2.1-6.0.0/add-permissions-rebuild.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

  create or replace function permissions_rebuild()
  returns integer
  as
  '
   declare
    row    record;
   begin
    delete from object_context_map;
    delete from granted_context_non_leaf_map;
    delete from ungranted_context_non_leaf_map;
    delete from object_grants;

    for row in select * from object_context
                where context_id is not null 
    loop
      perform permissions_add_context(row.object_id, row.context_id);
    end loop;

    for row in select * from acs_permissions 
    loop
      perform permissions_add_grant(row.object_id);
    end loop;
    return 1;
  end;' language 'plpgsql';
