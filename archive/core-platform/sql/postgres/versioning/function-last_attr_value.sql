-- The function that retrieves the last known value of an attribute.
-- It starts with start_transaction_id and backtracks through history,
-- finding the most recent record of changing the attribute and
-- returning it.

create or replace
function last_attr_value(varchar, integer)
returns varchar
as '
declare
  v_master_id integer;
  start_time date;
  end_time date;
  attr alias for $1;
  start_transaction_id alias for $2;
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
end ' language 'plpgsql';
