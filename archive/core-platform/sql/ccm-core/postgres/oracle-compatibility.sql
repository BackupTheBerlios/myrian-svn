--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/oracle-compatibility.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
