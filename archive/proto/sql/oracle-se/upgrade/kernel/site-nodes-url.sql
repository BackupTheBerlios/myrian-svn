alter table site_nodes modify url varchar(3000);

create unique index site_nodes_url_idx on site_nodes (url);