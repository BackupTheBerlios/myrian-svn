-----------
-- USERS --
-----------

create table users (
	user_id	    integer
                constraint users_user_id_nn
                not null
				constraint users_user_id_fk
				references parties (party_id)
				constraint users_pk primary key,
    name_id     integer
                constraint users_person_name_id_nn
                not null
				constraint users_person_name_id_fk
				references person_names(name_id)
				constraint users_person_name_id_un unique,
	screen_name		varchar(100)
				constraint users_screen_name_un
				unique
);
