-- populate web_hosts from publish_to_fs_servers if that table exists
create or replace function temp_update_web_hosts() returns boolean as '
declare
  v_exists boolean;
begin
  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''publish_to_fs_servers'';

  if (v_exists) then
    insert into web_hosts
      (host_id, server_name)
    (select id, hostname
      from publish_to_fs_servers);
  end if;

  return v_exists;
end;
' language 'plpgsql';

select temp_update_web_hosts();
drop function temp_update_web_hosts();
