-- populate web_hosts from publish_to_fs_servers if that table exists
declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'publish_to_fs_servers';

  if (v_exists = '1') then
    insert into web_hosts
      (host_id, server_name)
    (select id, hostname
      from publish_to_fs_servers);
    commit;
  end if;
end;
/
show errors;



