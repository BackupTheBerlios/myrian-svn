comment on table vc_operations is '
  An operation is a single modification made to an attribute of
  an object by the user. Transactions are sets of operations.
';
comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';
