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

-- ContentChangeTime-create.sql

-- Stores the time that any content last changed.
-- This written into when an object of type FlagChange is
-- created and comitted.  This table is examined by
-- BuildIndex.java which uses the largest value (time of
-- last change) to determine if the index should be
-- rebuilt.
-- Author: Jeff Teeters (teeters@arsdigita.com)

-- $Id: //core-platform/dev/sql/oracle-se/search/ContentChangeTime-create.sql#1 $


-- Time is stored as number of seconds since Jan 1, 1970

create table content_change_time (
   change_time            integer
);


