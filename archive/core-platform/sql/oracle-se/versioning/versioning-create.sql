--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- Creates tables neccesary for the Versioning object-level service
--
-- @author Joseph Bank (jbank@arsdigita.com)
-- @author Stanislav Freidin (sfreidin@arsdigita.com)
-- @author Karl Goldstein (karlg@arsdigita.com)
-- @version $Id: //core-platform/dev/sql/oracle-se/versioning/versioning-create.sql#4 $
--

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
  timestamp        date default sysdate,
  description      varchar(4000),
  tag              varchar(400)
);

-- index the foreign key
create index vc_transactions_master_id_idx on vc_transactions(master_id);
alter table vc_transactions modify timestamp not null;

-- index the timestamp -- avoids full table scans for last_attr_value.
create index vc_transactions_tstamp_idx on vc_transactions(timestamp);

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

create table vc_actions (
  action            varchar(200) 
    constraint vc_actions_pk primary key,
  description       varchar(4000)
);

comment on table vc_actions is '
  Defines all possible actions which may be performed on an
  attribute, such as creation, modification, deletion, etc.
';

declare
begin
  insert into vc_actions (action, description) 
  values (
    'create', 'Create a new value for the attribute'
  );

  insert into vc_actions (action, description) 
  values (
    'update', 'Update the attribute''s value (for single-valued attributes)'
  );

  insert into vc_actions (action, description) 
  values (
    'add', 'Add a value for a multi-valued attribute'
  );  

  insert into vc_actions (action, description) 
  values (
    'remove', 'Remove a value from a multi-valued attribute'
  );  

  insert into vc_actions (action, description) 
  values (
    'create_content', 'Create new content for the object'
  );  

  insert into vc_actions (action, description) 
  values (
    'update_content', 'Modify existing content for the object'
  );  
end;
/
show errors

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

-- index foreign keys
create index vc_operations_transaction_idx on vc_operations(transaction_id);
create index vc_operations_action_idx on vc_operations(action);

comment on table vc_operations is '
  An operation is a single modification made to an attribute of
  an object by the user. Transactions are sets of operations.
';

comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';

create table vc_generic_operations (
  operation_id      integer 
    constraint vc_gen_operations_pk primary key
    constraint vc_gen_operations_fk references vc_operations
    on delete cascade,
  datatype          varchar(200),
  old_value         varchar(4000),
  new_value         varchar(4000)
);

comment on column vc_generic_operations.old_value is '
  The old value of the attribute (could be null). Non-varchar values 
  (such as integers, dates, etc.) are coerced to a string format.
';

create table vc_clob_operations (
  operation_id      integer 
    constraint vc_clob_operations_pk primary key
    constraint vc_clob_operations_fk references vc_operations
    on delete cascade,
  old_value         clob,
  new_value         clob
);

create table vc_blob_operations (
  operation_id      integer 
    constraint vc_blob_operations_pk primary key
    constraint vc_blob_operations_fk references vc_operations
    on delete cascade,
  old_value         blob,
  new_value         blob
);

-- The function that retrieves the last known value of an attribute.
-- It starts with start_transaction_id and backtracks through history,
-- finding the most recent record of changing the attribute and
-- returning it.

create or replace
function last_attr_value(attr varchar, start_transaction_id in integer)
return varchar
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
