-- Create foreign key indexes
create index acs_auditing_creation_user_idx on acs_auditing(creation_user);
create index acs_auditing_modifyin_user_idx on acs_auditing(modifying_user);
