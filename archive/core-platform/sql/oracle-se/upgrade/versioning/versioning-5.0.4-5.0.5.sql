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
alter table vc_transactions 
    add constraint 
    vc_trans_objects_fk foreign key (object_id) references vc_objects;

alter table vc_transactions add (
    master_id  integer
               constraint vc_trans_masters_fk references vc_objects
               on delete cascade
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

update vc_operations vo
set classname = (decode(
    (select 1 from vc_generic_operations vgo
     where vgo.operation_id = vo.operation_id
     union all
     select 2 from vc_clob_operations vco
     where vco.operation_id = vo.operation_id
     union all
     select 3 from vc_blob_operations vbo
     where vbo.operation_id = vo.operation_id),
    1, 'com.arsdigita.versioning.GenericOperation',
    2, 'com.arsdigita.versioning.ClobOperation',
    3, 'com.arsdigita.versioning.BlobOperation'))
where classname is null;

alter table vc_operations modify (
    classname varchar2(4000) constraint vc_operations_classname_nn not null
);

comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';

