

create table vc_operations (
  operation_id      integer 
    constraint vc_operations_pk primary key,
  transaction_id    integer
    constraint vc_operations_trans_id_fk references vc_transactions
    on delete cascade,
  action            varchar(200)
    constraint vc_operations_actions_fk references vc_actions,
  attribute         varchar(200),
  classname         varchar(4000) 
    constraint vc_operations_classname_nn not null
);
