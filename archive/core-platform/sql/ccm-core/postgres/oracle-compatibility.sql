--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/oracle-compatibility.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

-- The equivalent of oracle's dual table.
create view dual as select 1;

-- A pseudo replacement for nvl. It doesn't have the same semantics
-- since nvl isn't a function in oracle but an operator, but it will
-- suffice in a large number of cases.
create or replace function nvl(varchar, varchar) returns varchar as '
begin
    if $1 is null then
        return $2;
    else
        return $1;
    end if;
end;
' language 'plpgsql';


-- Replacements for PG's special operators that cause persistence's
-- SQL parser to barf

create or replace function bitand(integer, integer) returns integer as '
begin
    return $1 & $2;
end;
' language 'plpgsql';

create or replace function bitor(integer, integer) returns integer as '
begin
    return $1 | $2;
end;
' language 'plpgsql';

create or replace function bitxor(integer, integer) returns integer as '
begin
    return $1 # $2;
end;
' language 'plpgsql';

create or replace function bitneg(integer) returns integer as '
begin
    return ~$1;
end;
' language 'plpgsql';
