-------------
-- PARTIES --
-------------

create table parties (
	party_id	    integer
                    constraint parties_party_id_nn
                    not null
			        constraint parties_party_id_fk references
			        acs_objects (object_id)
			        constraint parties_pk primary key,
    primary_email   varchar(100),
	uri		        varchar(200)
);
