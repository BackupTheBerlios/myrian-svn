create table note_themes (
    theme_id integer 
			constraint note_themes_theme_id_fk references
			acs_objects (object_id)
			constraint note_themes_pk primary key,
    name varchar(200) not null
	constraint note_themes_un unique
);
