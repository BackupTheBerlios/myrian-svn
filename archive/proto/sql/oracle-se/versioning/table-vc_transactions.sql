create table vc_transactions (
  transaction_id   integer 
    constraint vc_transactions_pk primary key,
  master_id        integer
    constraint vc_trans_masters_fk references vc_objects
    on delete cascade,
  object_id        integer
    constraint vc_trans_objects_fk references vc_objects
    on delete cascade,
  modifying_user   integer,
  modifying_ip     varchar(400),
  timestamp        date default sysdate not null,
  description      varchar(4000),
  tag              varchar(400)
);
