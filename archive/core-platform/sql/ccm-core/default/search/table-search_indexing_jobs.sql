--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/search/table-search_indexing_jobs.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table search_indexing_jobs (
    job_num         	integer
	          	constraint search_indexing_jobs_pk primary key,
    time_queued         date,
    time_started        date,
    time_finished       date,
    time_failed         date
);
