comment on table forms_lstnr_conf_email is '
  Stores details of the confirmation email to be sent
  upon submission.
';
comment on column forms_lstnr_conf_email.sender is '
  Email address of sender for confirmation email
';
comment on column forms_lstnr_conf_email.subject is '
  The subject line of the mail
';
comment on column forms_lstnr_conf_email.body is '
  The text of the email optionally containing
  placeholders of the form "::foo.bar::"
';
