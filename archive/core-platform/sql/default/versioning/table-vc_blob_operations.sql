create table vc_blob_operations (
  operation_id      integer 
    constraint vc_blob_operations_pk primary key
    constraint vc_blob_operations_fk references vc_operations
    on delete cascade,
  old_value         blob,
  new_value         blob
);
