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
