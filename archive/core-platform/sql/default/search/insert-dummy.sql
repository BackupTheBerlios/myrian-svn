--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/default/search/insert-dummy.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

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
