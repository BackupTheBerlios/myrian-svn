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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/kernel/core-platform-precheck-4.6.7-4.6.8.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


WHENEVER SQLERROR EXIT ROLLBACK;

-- check that there are no null passwords in the database.
declare
  v_user_id integer;
  v_display_name varchar2(1000);
begin
  select min(user_id) into v_user_id
  from user_authentication 
  where password is null;

  if (v_user_id is not null) then
    select given_name || ' ' || family_name || 
           ' (' || primary_email || '), screen name: ' || screen_name
    into v_display_name
    from parties, person_names, users
    where parties.party_id = users.user_id
      and person_names.name_id = users.name_id
      and users.user_id = v_user_id;

    RAISE_APPLICATION_ERROR(-20000, 
          'Upgrade cannot proceed if any users have null ' ||
          'passwords. The following user''s password is null: ' || 
          v_display_name || 
          '.  Reset the user''s password or delete the user before upgrading.'
    );
  end if;

end;
/

WHENEVER SQLERROR CONTINUE;
