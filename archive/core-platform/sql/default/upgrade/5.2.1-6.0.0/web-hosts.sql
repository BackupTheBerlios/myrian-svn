create table web_hosts (
    host_id INTEGER not null
        constraint web_hosts_host_id_p_r717b
          primary key,
    server_name VARCHAR(200)
        constraint web_hosts_server_name_u_frlsu
          unique,
    server_port INTEGER
);
