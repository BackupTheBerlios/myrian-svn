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
