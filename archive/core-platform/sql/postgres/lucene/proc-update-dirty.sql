

create or replace function lucene_update_dirty(timestamp, timestamp, integer, integer)
returns boolean
as '
declare
  oldTimestamp alias for $1;
  newTimestamp alias for $2;
  oldDirty alias for $3;
  newDirty alias for $4;
begin
  if date_trunc(''minute'', newTimestamp) < date_trunc(''minute'', oldTimestamp) then
    return oldDirty;
  else
    return newDirty;
  end if;
end;' language 'plpgsql';

    
