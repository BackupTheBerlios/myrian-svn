--------------------------------
-- KERNEL USER AUTHENTICATION --
--------------------------------

create table user_authentication (
	auth_id			integer not null
				constraint user_auth_pk primary key,
	user_id			integer not null
				constraint user_auth_user_id_fk
				references users (user_id)
				constraint user_auth_user_un unique,
	password		varchar(100) not null,
	salt			varchar(100),
	password_question	varchar(1000),
	password_answer		varchar(1000)
);
