--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/function-last_attr_value.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

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
