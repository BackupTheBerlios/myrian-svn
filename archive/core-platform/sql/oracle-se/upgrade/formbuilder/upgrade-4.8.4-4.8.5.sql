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
-- @version $Id: //core-platform/dev/sql/oracle-se/upgrade/formbuilder/upgrade-4.8.4-4.8.5.sql#1 $
--

-- Drop the tables referencing bebop_components before we drop
-- the bebop_components table
drop table bebop_form_hierarchy;
drop table bebop_widgets;
drop table bebop_form_sections;

alter table bebop_components add (
    admin_name          varchar2(100),
    description         varchar2(4000),
    active_p            char(1)
);

alter table bebop_components drop column class_name;
alter table bebop_components drop column default_value;

comment on table bebop_components is '
 Stores data for the Component Data Object (used by
 persistent Bebop Components).
';

comment on column bebop_components.admin_name is '
 A name that helps administrators identify the Component.
';

comment on column bebop_components.description is '
 As description that helps users use the Component.
';

comment on column bebop_components.attribute_string is '
 This is the attribute string of the Component on the XML attribute
 format 
        key1="value1" key2="value2" ... keyN="valueN"
';

comment on column bebop_components.active_p is '
 If this is true the component is active and will be displayed. By
 setting this column to false an admin has disabled a component without
 having to delete it and with the option of activating it later.
';

create table bebop_widgets (
    widget_id             integer
                          constraint bebop_widgets_id_fk
                          references bebop_components (component_id)
			              constraint bebop_widgets_pk
			              primary key,
    parameter_name        varchar2(100),
    parameter_model       varchar2(150),
    default_value         varchar2(4000)
);

comment on table bebop_widgets is '
 Stores data needed specificly to persisting objects of class
 Widget.
';

comment on column bebop_widgets.parameter_name is '
 We currently only support the StringParameter class for the
 parameter model of the widget. This is the name that this
 class takes in its constructor.
';

comment on column bebop_widgets.parameter_name is '
 If a process listener does not dictate a certain parameter model
 it might be desirable for an admin to be able to set one.
';

comment on column bebop_widgets.default_value is '
 This is the default value of the Component. This corresponds to
 the text between the tags or the value attribute in the XHTML representation.
';


create table bebop_options (
       option_id                  integer
                                  constraint bebop_options_id_pk
                                  primary key,
       parameter_name             varchar2(100),
       label                      varchar2(300)          
);

comment on table bebop_options is '
 As you will notice, bebop_options is similar to bebop_widgets. I contemplated
 modeling options as widgets. However, Bebop didnt choose to do this and in the interest
 of mimicking Bebop as closely and possible I am letting Options be its own data type
 that extends Component. Options are mapped to Widgets via the table
 bebop_component_hierarchy.
';

comment on column bebop_options.parameter_name is '
 This is the HTML name of the option (identical to that of a Widget).
';

comment on column bebop_options.label is '
 This is the label of the option that is displayed to the user.
';

create table bebop_form_sections (
       form_section_id           integer
                                 constraint bebop_form_sections_id_fk
                                 references bebop_components (component_id)
                                 constraint bebop_form_sections_pk
                                 primary key,
       action                    varchar2(500)
);

comment on table bebop_form_sections is '
 This table contains some essential attributes particular to a Bebop 
 FormSection. 
';

comment on column bebop_form_sections.action is '
 This is the form HTML attribute action. You might think it should be stored in
 the attribute_string of the bebop_components table, and so do I. However,
 Bebop treats the action as a special case and doesnt store it as the other
 attributes.
';


create table bebop_process_listeners (
	  listener_id		integer
                        constraint bebop_process_listeners_fk
                        references acs_objects (object_id) on delete cascade
                        constraint bebop_process_listeners_pk
                        primary key,
      name              varchar2(40),
      description       varchar2(120),
      listener_class    varchar2(100)
);

comment on table bebop_process_listeners is '
  This table contains the persistent data for process listeners
';

comment on column bebop_process_listeners.listener_id is '
  This is the unique object id for this process listener.
';

comment on column bebop_process_listeners.name is '
  The user supplied name of this form process listener.
';

comment on column bebop_process_listeners.description is '
  The user supplied, long description of the purpose of the
  process listener
';

create table bebop_form_process_listeners (
        form_section_id         integer
                                constraint bebop_form_process_lstnr_fs_fk
                                references bebop_form_sections on delete cascade,
        listener_id             integer
                                constraint bebop_form_process_lstnr_li_fk
                                references bebop_process_listeners on delete cascade,
        position                integer,
        constraint bebop_form_process_lstnr_pk
        primary key (form_section_id, listener_id),
        constraint bebop_form_process_lstnr_un
	unique (form_section_id, position)
);


comment on table bebop_form_process_listeners is '
  This table maps process listeners to individual forms.
';

comment on column bebop_form_process_listeners.form_section_id is '
  The oid of the persistent form object.
';

comment on column bebop_form_process_listeners.listener_id is '
  The oid of the process listener object.
';

comment on column bebop_form_process_listeners.position is '
  Stores the position in which the listener was added to the
  form section.
';

create table bebop_component_hierarchy (
       container_id              integer
                                 constraint bebop_component_hierarchyci_fk
                                 references bebop_components (component_id),
       component_id              integer
                                 constraint bebop_component_hierarchyco_fk
                                 references bebop_components(component_id),
       order_number              integer,
       selected_p                char(1),
       constraint bebop_component_hierarchy_un
       unique(container_id, component_id)
);

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

create table bebop_listeners (
       listener_id           integer
                             constraint bebop_listeners_id_fk
                             references acs_objects(object_id)
                             constraint bebop_listeners_id_pk
                             primary key,
       class_name            varchar2(150),
       attribute_string      varchar2(4000)
);

comment on table bebop_listeners is '
 For storing listener classes that are added to form sections or widgets.
 The table is used for listener types that can have more than one instenance
 mapped to a component. An exception is the PrintListener since a Widget
 can have only one Printlistener.
';

comment on column bebop_listeners.class_name is '
 The class name of the listener. Lets you persist any listener. Precondition is
 that the listener has a default constructor. No attributes will be set.
';

comment on column bebop_listeners.attribute_string is '
 For persistent listeners that need store attributes. Is on XML attribute format
 just like the column in bebop_components.
';

create table bebop_listener_map (
       component_id          integer
                             constraint bebop_listener_map_cid_fk
                             references bebop_components(component_id),
       listener_id           integer
                             constraint bebop_listener_map_lid_fk
                             references bebop_listeners(listener_id),
       constraint bebop_listener_map_un
       unique(component_id, listener_id)
);

comment on table bebop_listener_map is '
 We use this table to map listeners to components.
';
