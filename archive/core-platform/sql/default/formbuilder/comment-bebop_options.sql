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
