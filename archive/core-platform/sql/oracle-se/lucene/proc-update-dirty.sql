

create or replace function lucene_update_dirty(oldTimestamp date, newTimestamp date, oldDirty integer, newDirty integer)
return integer
as begin
  if newTimestamp < oldTimestamp then
    return oldDirty;
  else
    return newDirty;
  end if;
end;
/

show errors

