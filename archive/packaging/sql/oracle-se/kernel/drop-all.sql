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
-- $Id: //core-platform/test-packaging/sql/oracle-se/kernel/drop-all.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $



--
-- c:/tinman/enterprise/dev/kernel/sql/oracle-se/drop-all.sql
-- 
-- Utility function dropping all objects in a user's schema.
--
-- @author Bryan Quinn (bquinn@arsdigita.com) 
-- @creation-date July 22, 2001 17:29:09
-- @cvs-id $Id: //core-platform/test-packaging/sql/oracle-se/kernel/drop-all.sql#1 $

begin
   ctx_ddl.drop_section_group('autogroup');
END;
/

declare
    cursor objects is
        select object_name, object_type
        from user_objects;
begin
    for object in objects loop
        begin
            if object.object_type = 'TABLE' then
                execute immediate 'drop table ' || object.object_name || ' cascade constraints';
            elsif object.object_type = 'SEQUENCE' or object.object_type = 'INDEX' or object.object_type = 'FUNCTION' or
                object.object_type = 'PROCEDURE' or object.object_type = 'VIEW' or object.object_type = 'PACKAGE' then
                execute immediate 'drop ' || object.object_type || ' ' || object.object_name;
            elsif object.object_type = 'UNDEFINED' and object.object_name like '%_MV' then
                execute immediate 'drop materialized view ' || object.object_name;
            end if;
        exception when others then
            null;
        end;
    end loop;
end;
/
show errors
