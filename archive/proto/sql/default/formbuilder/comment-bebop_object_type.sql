comment on table bebop_object_type is '
  This table defines the base object types whose subclasses we
  need to store meta data on.
';
comment on column bebop_object_type.type_id is '
  The unique indentifier for the object base type. This does
  not need to be a sequence since the table is statically
  populated at install time.
';
comment on column bebop_object_type.app_type is '
  The type of application using the object type.
';
comment on column bebop_object_type.class_name is '
  The fully qualified java class name of the base type.
';
