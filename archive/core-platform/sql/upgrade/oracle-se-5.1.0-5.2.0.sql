--
-- Copyright (C) 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the ACS Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/acspl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
--
-- $Id: //core-platform/dev/sql/upgrade/oracle-se-5.1.0-5.2.0.sql#4 $

create or replace function currentDate
return date 
as 
begin 
   return sysdate;
end;
/ 
show errors;

@@ ../oracle-se/upgrade/categorization/categorization-5.2.1-5.2.2.sql

drop table secret_tokens;