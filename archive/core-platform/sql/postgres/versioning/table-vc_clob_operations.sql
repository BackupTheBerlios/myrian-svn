create table vc_clob_operations (
  operation_id      integer 
    constraint vc_clob_operations_pk primary key
    constraint vc_clob_operations_fk references vc_operations
    on delete cascade,
  old_value         text,
  new_value         text
);
