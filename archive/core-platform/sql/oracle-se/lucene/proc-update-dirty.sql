--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/oracle-se/lucene/proc-update-dirty.sql#5 $
-- $DateTime: 2003/08/15 13:46:34 $



create or replace function lucene_update_dirty(oldTimestamp date, newTimestamp date, oldDirty integer, newDirty integer)
return integer
as begin
  if newTimestamp < oldTimestamp then
    return oldDirty;
  else
    if newDirty <> 2147483647 then
        return bitand(newDirty, oldDirty);
    else
        return newDirty;
    end if;
  end if;
end;
/

show errors
