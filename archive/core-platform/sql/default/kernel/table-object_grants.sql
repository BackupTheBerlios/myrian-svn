-- This table holds a count of grants for each object, and is only
-- used in order to maintain the *trans_context_index denormalizations
create table object_grants (
       object_id            integer not null
                            constraint object_grants_object_id_fk 
                            references acs_objects (object_id)
                            constraint object_grants_pk primary key,
       n_grants             integer not null
                            constraint object_grants_positive_ck
                                check (n_grants >= 1)
) organization index;
