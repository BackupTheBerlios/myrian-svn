-- do we point to the app instance w/ something like context_id?
create table notes (
    note_id integer 
			constraint notes_note_id_fk references
			acs_objects (object_id)
			constraint notes_pk primary key,
-- will add later
--    owner_id integer 
--          constraint notes_owner_id_fk references users(user_id),
-- add not null later to the users...
    title varchar(255) not null,
    body varchar(4000),
	theme_id integer
        constraint notes_theme_id_fk references
		note_themes(theme_id)
);
