create table party_email_map (
	party_id	    integer not null
    			    constraint pem_party_id_fk
	        		    references parties(party_id) on delete cascade,
    email_address   varchar(100),
    constraint pem_party_email_uq
                    unique(party_id, email_address)
);
