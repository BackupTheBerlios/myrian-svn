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
-- Copyright (C) 2001 Arsdigita Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/permissions-4.8.10-4.8.11.sql#1 $

-- Add cascading deletes to grantee_id and privilege columns
alter table acs_permissions
  drop constraint acs_permissions_grantee_id_fk;
alter table acs_permissions 
  drop constraint acs_permissions_priv_fk;
  
alter table acs_permissions 
  add constraint acs_permissions_priv_fk
      foreign key (privilege) references acs_privileges (privilege) 
      on delete cascade;
alter table acs_permissions 
  add constraint acs_permissions_grantee_id_fk
      foreign key (grantee_id) references parties (party_id) 
       on delete cascade;

-- Experimental: parameterized privileges such as "Create bboard messages"
create table parameterized_privileges (
    base_privilege varchar(100) not null
                   constraint param_priv_base_privilege_fk
                       references acs_privileges(privilege),
    param_key      varchar(100) not null,
    param_name     varchar(100),
    constraint param_priv_un unique (param_key, base_privilege)
);
