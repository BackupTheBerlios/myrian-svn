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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/5.2.1-6.0.0/misc.sql#1 $
-- $DateTime: 2003/08/04 15:56:00 $

drop function last_attr_value;

alter table cat_categories modify (
    abstract_p not null,
    enabled_p not null
);

alter table portlets modify (
    portal_id not null
);

drop table cw_process_task_map;

alter table cat_categories modify (
    default_ancestors varchar2(3209)
);

declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_indexes uc
   where lower(index_name) = 'cat_cat_deflt_ancestors_idx';

  if (v_exists = '0') then
    execute immediate 'create index cat_cat_deflt_ancestors_idx on cat_categories(default_ancestors)';
  end if;

end;
/
show errors;
