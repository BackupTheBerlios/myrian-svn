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
-- $Id: //core-platform/test-qgen/sql/ccm-core/oracle-se/upgrade/5.2.1-6.0.0/update-web-hosts.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

declare
  v_exists char(1);
begin
  select decode (count(*),0,'f','t') into v_exists
    from user_tables
   where lower(table_name) = 'publish_to_fs_servers';

  if (v_exists = 't') then
    execute immediate 'insert into web_hosts (host_id, server_name) (select id, hostname from publish_to_fs_servers)';
    commit;
  end if;
end;
/
show errors;
