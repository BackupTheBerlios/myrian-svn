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

-- Upgrades the data model from version 4.8.10 to 4.8.11
--
-- Copyright (C) 2002 Arsdigita Corporation
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/acs-stylesheet-4.8.10-4.8.11.sql#1 $

-- Drop the foreign key constraints and then re-enable them with 
-- with on delete cascade set.

alter table acs_stylesheet_node_map
  drop constraint acs_stylesheet_node_sheet_fk;
alter table acs_stylesheet_node_map 
  drop constraint acs_stylesheet_node_node_fk;
  
alter table acs_stylesheet_node_map 
  add constraint acs_stylesheet_node_sheet_fk
  foreign key (stylesheet_id) references 
  acs_stylesheets (stylesheet_id) on delete cascade;
alter table acs_stylesheet_node_map 
  add constraint acs_stylesheet_node_node_fk
  foreign key (node_id) references 
  site_nodes (node_id) on delete cascade;


alter table acs_stylesheet_type_map
  drop constraint acs_stylesheet_node_type_fk;
alter table acs_stylesheet_type_map 
  drop constraint acs_stylesheet_type_type_fk;
  
alter table acs_stylesheet_type_map 
  add constraint acs_stylesheet_type_sheet_fk
  foreign key (stylesheet_id) references 
  acs_stylesheets (stylesheet_id) on delete cascade;
alter table acs_stylesheet_type_map 
  add constraint acs_stylesheet_type_type_fk
  foreign key (package_type_id) references 
  apm_package_types (package_type_id) on delete cascade;
