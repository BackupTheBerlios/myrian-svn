-- populate web_hosts from publish_to_fs_servers if that table exists
declare
  v_exists char(1);
begin
  select decode (count(*),0,'f','t') into v_exists
    from user_tables
   where lower(table_name) = 'publish_to_fs_servers';

  if (v_exists = 't') then
    execute immediate 'insert into web_hosts (host_id, server_name) (select id, hostname from publish_to_fs_servers)';
    commit;
  end if;
end;
/
show errors;



