-- This table holds the mappings between object and implied contexts
-- where the implied contexts have *no* direct grant(s).
create table ungranted_context_non_leaf_map (
       object_id            integer not null
                            constraint ucnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   constraint ucnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint ucnlm_generation_ck
                                check (n_generations >= 0),
       constraint ucnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
) organization index;
