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
