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
