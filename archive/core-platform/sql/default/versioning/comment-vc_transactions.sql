comment on table vc_transactions is '
  A transaction is a set of modifications that was made to an object''s
  attributes by a user during a database transaction. 
';
comment on column vc_transactions.master_id is '
  The ID of the top-level master object for this transaction
';
comment on column vc_transactions.object_id is '
  The ID of the object which was actually modified during the transaction
';
