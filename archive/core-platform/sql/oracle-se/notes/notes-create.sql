--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

-- Data model to support sample notes application

-- Copyright (C) 2001 ArsDigita Corporation
-- Authors: Scott Seago (scott@arsdigita.com)
--	    Tzu-Mainn Chen (tzumainn@arsdigita.com)

-- notes-create.sql,v 1.5 2001/05/29 21:46:54 scott Exp

create table note_themes (
    theme_id integer 
			constraint note_themes_theme_id_fk references
			acs_objects (object_id)
			constraint note_themes_pk primary key,
    name varchar(200) not null
	constraint note_themes_un unique
);

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

create table theme_stylesheet_map (
    theme_id constraint tsm_theme_id_fk references note_themes(theme_id),
    stylesheet_id constraint tsm_stylesheet_id_fk 
                references acs_stylesheets(stylesheet_id),
    primary key (theme_id, stylesheet_id)
);
