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
-- $Id: //core-platform/dev/sql/default/formbuilder/comment-bebop_component_hierarchy.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

comment on table bebop_component_hierarchy is '
 This table contains information about the Component hierarchy 
 contained in a Bebop form. Examples of relationships stored in this table
 are that between a FormSection and its Widgets and that between
 an OptionGroup and its Options.
';
comment on column bebop_component_hierarchy.container_id is '
 This is the component id of the containing component. Examples include FormSection
 and OptionGroup.
';
comment on column bebop_component_hierarchy.component_id is '
 This will typically be a Bebop Widget or another type of Component 
 used in Forms, for example a Label.
';
comment on column bebop_component_hierarchy.order_number is '
 This is the order in which the components were added to their container.
';
comment on column bebop_component_hierarchy.selected_p is '
 OptionGroups need to store information about which Options are selected.
';
