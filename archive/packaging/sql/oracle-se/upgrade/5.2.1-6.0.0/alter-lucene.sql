
alter table lucene_docs drop column is_dirty;
alter table lucene_docs add dirty integer;
update lucene_docs set dirty = 2147483647;
alter table lucene_docs modify dirty not null;

create or replace function lucene_update_dirty(oldTimestamp date, newTimestamp date, oldDirty integer, newDirty integer)
return integer
as begin
  if newTimestamp < oldTimestamp then
    return oldDirty;
  else
    if newDirty <> 2147483647 then
        return bitand(newDirty, oldDirty);
    else
        return newDirty;
    end if;
  end if;
end;
/

show errors
