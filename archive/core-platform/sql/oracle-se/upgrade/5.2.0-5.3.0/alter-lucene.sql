
alter table lucene_docs drop column is_dirty;
alter table lucene_docs add dirty integer;
update lucene_docs set dirty = 2147483647;
alter table lucene_docs modify dirty not null;
