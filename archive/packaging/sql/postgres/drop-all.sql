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
-- $Id: //core-platform/test-packaging/sql/postgres/drop-all.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

create or replace function drop_all(varchar) returns boolean as '
declare
    username alias for $1;
    drop_type varchar;
    row record;
begin
    for row in select *
               from pg_class, pg_user
               where pg_class.relowner = pg_user.usesysid
               and cast(usename as varchar) = username
               and relkind in (''r'', ''v'', ''S'')
               and relname not like ''pg_%'' loop
        if row.relkind = ''r'' then
          drop_type := ''table'';
        elsif row.relkind = ''v'' then
          drop_type := ''view'';
        elsif row.relkind = ''S'' then
          drop_type := ''sequence'';
        else
          drop_type := ''none'';
        end if;

        execute ''drop '' || drop_type || '' '' || row.relname;
    end loop;

    return true;
end;
' language 'plpgsql';

select drop_all(user());
