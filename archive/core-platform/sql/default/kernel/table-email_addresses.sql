create table email_addresses (
	email_address   varchar(100) not null
                    constraint email_addresses_pk primary key
                    constraint email_address_lower_ck
                        check (lower(email_address) = email_address),
	-- TO DO: these should be non-nullable and default to whatever
	-- JDBC's version of false is.
	bouncing_p	char(1),
	verified_p	char(1)
);
