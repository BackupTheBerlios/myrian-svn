comment on table vc_objects is '
  Tracks all the objects that are versioned.
';
comment on column vc_objects.is_deleted is '
  If true (1), the object has been deleted and cannot be successfully
  retrieved by normal means.
';
comment on column vc_objects.master_id is '
  The master object for this versioned object; that is, the
  very top-level object of which this object is a composite.
  Used by the versioning system to keep track of transactions
  for an object
';

