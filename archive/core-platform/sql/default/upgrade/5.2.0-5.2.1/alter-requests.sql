alter table nt_requests drop constraint nt_requests_message_fk;

alter table nt_requests add constraint nt_requests_message_fk
  foreign key (message_id) references messages (message_id)
  on delete cascade;
  
