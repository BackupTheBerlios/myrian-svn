--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_parties.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $
create or replace function upgrade_dnm_parties ()
  returns integer as '
  declare 
    c record;
  begin
   for c in select grantee_id from acs_permissions loop
     perform dnm_parties_add_grant(c.grantee_id);
   end loop;

   for c in select group_id, subgroup_id  from group_subgroup_map loop 
     perform dnm_parties_add_group_subgroup_map(c.group_id, c.subgroup_id);
   end loop;

   for c in select group_id, member_id from group_member_map loop
     perform dnm_parties_add_group_user_map (c.group_id, c.member_id);
   end loop;
   
   return null;
end; ' language 'plpgsql'
;

select upgrade_dnm_parties();

drop function upgrade_dnm_parties();
