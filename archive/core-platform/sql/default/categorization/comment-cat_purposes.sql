comment on table cat_purposes is '
   Used to define what the various top-level branches 
   (i.e. direct children of the "/" category) are to be used for
';
comment on column cat_purposes.key is '
   Unique integer defined via static final ints in the Java domain class
';
comment on column cat_purposes.name is '
   Name is used to display the category purpose in select lists, etc.
';
