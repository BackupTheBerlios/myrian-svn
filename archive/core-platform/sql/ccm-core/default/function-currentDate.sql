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
-- $Id: //core-platform/dev/sql/ccm-core/default/function-currentDate.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

-- This is a function to allow the postgres and oracle PDL to 
-- not have to hard code current_timestamp() and sysdate
-- respectively
create or replace function currentDate()
  returns timestamptz as '
  declare
  begin
    return current_timestamp;
end;' language 'plpgsql';
