create table search_indexing_jobs (
    job_num         	integer
	          	constraint search_indexing_jobs_pk primary key,
    time_queued         date,
    time_started        date,
    time_finished       date,
    time_failed         date
);
