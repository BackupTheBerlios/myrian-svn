

create or replace function lucene_update_dirty(oldTimestamp date, newTimestamp date, oldDirty char, newDirty char)
return char
as begin
  if newTimestamp < oldTimestamp then
    return oldDirty;
  else
    return newDirty;
  end if;
end;
/

show errors
