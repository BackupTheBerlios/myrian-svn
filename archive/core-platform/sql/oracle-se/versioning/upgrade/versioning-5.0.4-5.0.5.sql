create table vc_objects (
  object_id       integer
    constraint vc_objects_obj_fk references acs_objects
    on delete cascade,
  is_deleted      char(1) default '0' not null
                  check (is_deleted in ('1', '0')),
  master_id       integer
    constraint vc_objects_mst_fk references acs_objects
    on delete set null,
  constraint vc_objects_pk
    primary key(object_id)
);

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

alter table vc_transactions drop constraint vc_trans_objects_fk;

alter table vc_transactions add (
    master_id  integer
);

comment on column vc_transactions.master_id is '
  The ID of the top-level master object for this transaction
';

comment on column vc_transactions.object_id is '
  The ID of the object which was actually modified during the transaction
';


drop index vc_transactions_object_id_idx;
create index vc_transactions_master_id_idx on vc_transactions(master_id);

alter table vc_operations add (
    classname varchar2(4000) 
);

comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';

create index vc_transactions_tstamp_idx on vc_transactions(timestamp);

create or replace
function last_attr_value(attr varchar2, start_transaction_id in integer)
return varchar2
is
  v_master_id integer;
  start_time date;
  end_time date;
begin
  -- The caller of this function already knows the master_id.  We
  -- could add an optional parameter that, when provided enables us to
  -- avoid this query.

  select master_id, timestamp into v_master_id, start_time
    from vc_transactions
    where transaction_id = start_transaction_id;

  declare
    cursor c is
      select new_value
        from vc_transactions t, vc_operations o, vc_generic_operations go
        where t.master_id = v_master_id
              and t.timestamp <= start_time
              and t.transaction_id = o.transaction_id
              and o.operation_id = go.operation_id
              and o.attribute = attr
              and go.new_value is not null
        order by t.timestamp desc;
  begin
    for row in c loop
      return row.new_value;
    end loop;
  end;

  return null;
end last_attr_value;
/
show errors
