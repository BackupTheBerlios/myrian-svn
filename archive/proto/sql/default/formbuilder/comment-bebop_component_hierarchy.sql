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
