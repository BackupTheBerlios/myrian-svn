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
-- This file will create the entire datamodel of the Form Builder
-- service
--
-- @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/formbuilder/formbuilder-drop.sql#1 $
--

-- Drop the tables referencing bebop_components before we drop
-- the bebop_components table
drop table bebop_listener_map;
drop table bebop_component_hierarchy;
drop table bebop_widgets;
drop table bebop_form_process_listeners;
drop table bebop_process_listeners;
drop table bebop_form_sections;
drop table bebop_components;
drop table bebop_options;
drop table bebop_listeners;
