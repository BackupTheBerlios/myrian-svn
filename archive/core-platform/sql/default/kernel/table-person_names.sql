------------------
-- PERSON_NAMES --
------------------

create table person_names (
	name_id	        integer not null
                        constraint person_names_pk primary key,
	given_name	varchar(60) not null,
	family_name	varchar(60) not null,
	middle_names	varchar(80)
);
