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
               and relkind in (''r'', ''v'', ''i'', ''S'')
               and relname not like ''pg_%'' loop
        if row.relkind = ''r'' then
          drop_type := ''table'';
        elsif row.relkind = ''v'' then
          drop_type := ''view'';
        elsif row.relkind = ''i'' then
          drop_type := ''index'';
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
