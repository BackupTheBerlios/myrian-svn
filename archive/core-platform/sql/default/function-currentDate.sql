--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/default/function-currentDate.sql#5 $
-- $DateTime: 2003/08/15 17:18:56 $

-- This is a function to allow the postgres and oracle PDL to 
-- not have to hard code current_timestamp() and sysdate
-- respectively
create or replace function currentDate()
  returns timestamptz as '
  declare
  begin
    return current_timestamp;
end;' language 'plpgsql';
