-- Insert dummy record to make sure something in table.
-- If tests are run with an empty table, will give following error:
-- PersistenceException: Unable to retrieve attribute oracleSysdate
-- of object type IndexingTime because there is no retrieve attributes 
-- event handler defined for it.

insert into  search_indexing_jobs
(job_num, time_queued, time_started, time_finished)
values
(-1, to_date('01-01-1970','mm-dd-yyyy'), 
    to_date('01-01-1970','mm-dd-yyyy'), 
    to_date('01-01-1970','mm-dd-yyyy'));
