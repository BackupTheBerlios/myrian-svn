comment on table parties is '
 Party is the supertype of user and group. It exists because
 many other types of object can have relationships to parties.
';
comment on column parties.primary_email is '
 Stores a reference to the party''s primary email address.
';
comment on column parties.uri is '
 This URI is a user-specified URI for the party.  E.g., a personal web page,
 a company web site, etc.
';
