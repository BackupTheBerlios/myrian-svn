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

-- Upgrade to new version of search indexing procs.

drop table search_indexing_jobs;
drop package search_indexing;

-- Table for keeping track of search indexing jobs

create table search_indexing_jobs (
    job_num         	integer
	          	constraint search_indexing_jobs_pk primary key,
    time_queued         date,
    time_started        date,
    time_finished       date,
    time_failed         date
);

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


create or replace package search_indexing
as                                                                              
    function queue_task (                                                 
	v_task_name in varchar2
    ) return integer;   -- returns 0 if failed, or jobnum
 
    function get_status 
    return varchar2;  -- returns status code

    procedure build_index;
    procedure sync_index;

    procedure run_foreground;

    function  job_number 
    return integer;  -- returns latest job number

    procedure flag_starting (
	job_n      in integer
    );

    procedure flag_finished (
	job_n      in integer
    );

end search_indexing;
/


create or replace package body search_indexing as

    -----------------------------------------------------
    -- QUEUE_TASK - queue a search task.  Task is either 'build' or 'sync'
    -----------------------------------------------------

    function queue_task (                                                 
       v_task_name     in   varchar2
    )  return integer 
    is
       job_n          integer;   -- job number
       job_status     varchar2(100);
       proc_call      varchar2(100);
    begin
       -- make sure valid task name
       if (v_task_name <> 'build' and v_task_name <> 'sync') then
          raise_application_error(-20000,
            ('Error in call to search_indexing.queue_task: ' ||
             'parameter must be "build" or "sync", not "' ||
                   v_task_name || '"'));
       end if;
       -- Lock search_indexing_jobs table
       lock table search_indexing_jobs in exclusive mode;

       -- Make sure previous job is not queued or running
       job_status := get_status();
       if (job_status = 'queued' or job_status = 'running') then
          -- Can't start job, previous job still running.
          -- release lock and return 0 (indicates failure)
	  commit;
          return 0;
       end if;
       
       -- queue job to execute once.  
       -- See: http://hojohn.photo.net/ora817/DOC/server.817/a76956/jobq.htm#966
       proc_call := 'search_indexing.' || v_task_name || '_index;';
       dbms_job.submit(job_n, proc_call, sysdate, null);

       -- save job number in search_indexing_jobs
       insert into search_indexing_jobs
          ( job_num, time_queued )
          values
          ( job_n, sysdate );

       -- Release lock on table and return job number
	  commit;	
          return job_n;
    end queue_task;


    -----------------------------------------------------
    -- GET_STATUS - Return the status of the most recently queued task
    -----------------------------------------------------
 
    function get_status
       return varchar2
    is
       job_n          number;
       cursor job_times_cur is
          select 
             time_queued, time_started, time_finished, time_failed
          from  
             search_indexing_jobs
          where 
             job_num = get_status.job_n;
       failed        integer;
       running        integer;
       job_times      job_times_cur%ROWTYPE;
       t_finished     date;
    begin
       -- Get number of latest job
       select max(job_num) into job_n
          from  search_indexing_jobs;
      
       if job_n is null then
          return 'finished';         -- flag that nothing running
       end if;

       -- Get information about latest job
       open job_times_cur;
       fetch job_times_cur into job_times;
       close job_times_cur;  

       -- Check various cases
       if job_times.time_finished is not null then
          return 'finished';
       end if;
       if job_times.time_failed is not null then
          return 'failed';
       end if;
       if job_times.time_started is null then
          -- was queued but never started
          return 'queued';
       end if;

       --Check in user_jobs to see if this job has
       --failed. 
       --If we haven't failed, we must be running!
       select failures into failed
          from user_jobs where job = job_n;
       if failed is null then
          return 'running';
       end if;

       -- Job still has not finished, must have failed
       -- flag job as broken so Oracle will not try to run it again
       DBMS_JOB.BROKEN(job_n, TRUE);
       update search_indexing_jobs
          set time_failed = sysdate
          where job_num = get_status.job_n;
       return 'failed';
       -- Note: information about jobs that failed are stored in
       -- a "trace" file and in the alert log.  See:
       -- http://hojohn.photo.net/ora817/DOC/server.817/a76956/jobq.htm#966
       --
       -- Information about indexing that failed may be
       -- obtained from the CTX_USER_INDEX_ERRORS table
       -- e.g. select err_timestamp, err_text 
       --      from CTX_USER_INDEX_ERRORS order by err_timestamp desc;
       -- see: http://hojohn.photo.net/ora817/DOC/inter.817/a77063/csql2.htm#14189
    end get_status;


    -----------------------------------------------------
    -- JOB_NUMBER - Return the number of the latest job
    -----------------------------------------------------

    function  job_number
     return integer
    is
       job_n     integer;
    begin
       select max(job_num) into job_n
       from  search_indexing_jobs;
       return job_n;
    end;

    -----------------------------------------------------
    -- RUN_FOREGROUND - Force the job to run foreground
    -----------------------------------------------------
    procedure run_foreground 
    is
       job_n integer;
       n integer;
    begin
       job_n := search_indexing.job_number();
       select count(*) into n from user_jobs where job = job_n;
       if n = 1 then
          dbms_job.run(job_n);
       end if;
    end;

    -----------------------------------------------------
    -- FLAG_STARTING - Records time indexing operation starting
    -----------------------------------------------------

    procedure flag_starting (
	job_n    in  integer
    )
    is
    begin
       update search_indexing_jobs
          set time_started = sysdate
          where job_num = job_n;
    end flag_starting;


    -----------------------------------------------------
    -- FLAG_FINISHED - Records time index operation finished
    -----------------------------------------------------

    procedure flag_finished (
	job_n    in  integer
    )
    is
    begin
       update search_indexing_jobs
          set time_finished = sysdate
          where job_num = job_n;
    end flag_finished;


    -----------------------------------------------------
    -- BUILD_INDEX - Drop and rebuild the search indices
    -----------------------------------------------------
    procedure build_index is
	job_n     integer;
    begin
        -- Flag job has started
        job_n := search_indexing.job_number();
        flag_starting(job_n);
        commit;

        -- First drop indexes
        execute immediate 'drop index xml_content_index';
        execute immediate 'drop index raw_content_index';

        -- Then recreate
        execute immediate 'CREATE INDEX xml_content_index ON ' ||
	     'search_content(xml_content) INDEXTYPE IS ctxsys.context ' ||
	     'parameters(''filter ctxsys.null_filter section group autogroup'')';
	execute immediate 'CREATE INDEX raw_content_index ON ' ||
	     'search_content(raw_content) INDEXTYPE IS ctxsys.context';

	-- Flag job has finished
	flag_finished(job_n);
        commit;
    end build_index;


    -----------------------------------------------------
    -- SYNC_INDEX - Resync the search indices without rebuilding
    -----------------------------------------------------
    procedure sync_index is
	job_n     integer;
    begin
        -- Flag job has started
        job_n := search_indexing.job_number();
        flag_starting(job_n);
        commit;

	-- rebuild indices
        execute immediate 'alter index xml_content_index rebuild online parameters (''sync'')';
        execute immediate 'alter index raw_content_index rebuild online parameters (''sync'')';

	-- Flag job has finished
	flag_finished(job_n);
        commit;
    end sync_index;

end search_indexing;
/
show errors;
