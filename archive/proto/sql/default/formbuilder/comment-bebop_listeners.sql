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
