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
-- $Id: //core-platform/test-qgen/sql/ccm-core/postgres/upgrade/5.2.1-6.0.0/alter-lucene.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

alter table lucene_docs drop column is_dirty;
alter table lucene_docs add dirty integer;
update lucene_docs set dirty = 2147483647;
alter table lucene_docs alter dirty set not null;

create or replace function lucene_update_dirty(timestamptz, timestamptz, integer, integer)
returns integer
as '
declare
  oldTimestamp alias for $1;
  newTimestamp alias for $2;
  oldDirty alias for $3;
  newDirty alias for $4;
begin
  if date_trunc(''minute'', newTimestamp) < date_trunc(''minute'', oldTimestamp) then
    return oldDirty;
  else
    if newDirty <> 2147483647 then
        return bitand(newDirty, oldDirty);
    else
        return newDirty;
    end if;
  end if;
end;' language 'plpgsql';
