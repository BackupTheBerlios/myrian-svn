-----------------------------
-- CONTEXT DENORMALIZATION --
-----------------------------

-- context hierarchy is denormalized into 2 tables.
-- The union of these 2 tables contains mapping from objects to their
-- implied contexts.  Every object has itself as an implied context, BUT
-- this implicit mapping is only entered into these denormalizations when
-- the object in question has a permission granted on it.
-- The structure of these denormalizaitons is primarily geared towards
-- optimization of permissions checks.  A secondary objective is to
-- minimize the cost of inserting objects, setting their context, and
-- granting permissions on them.  Finally, these denormalizations may
-- prove useful for permissions UI, e.g., "display all objects that
-- inherit permissions from X".

-- This table holds the mappings between object and implied contexts
-- where the implied contexts have direct grant(s).
create table granted_context_non_leaf_map (
       object_id            integer not null
                            constraint gcnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   constraint gcnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint gcnlm_generation_ck
                                check (n_generations >= 0),
       constraint gcnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
) organization index;
