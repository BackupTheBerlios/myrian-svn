create table vc_generic_operations (
  operation_id      integer 
    constraint vc_gen_operations_pk primary key
    constraint vc_gen_operations_fk references vc_operations
    on delete cascade,
  datatype          varchar(200),
  old_value         varchar(4000),
  new_value         varchar(4000)
);
