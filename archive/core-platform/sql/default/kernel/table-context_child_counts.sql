-- this table holds a count of "children" for each object, and is only
-- used in order to maintain the *trans_context_index denormalizations

-- This table has been removed because it causes contention when
-- inserting two objects that share the same context. Both try to
-- update this table and so the operations become serialized. This is
-- temporarily replaced with a select from object_context_map, but
-- since I believe we want to eventually remove object_context_map in
-- favor of something that doesn't store the mapping for leaf nodes,
-- I'm leaving this here.

--create table context_child_counts (
--   object_id  integer not null
--              constraint ccc_object_id_fk
--              references acs_objects (object_id)
--              constraint ccc_object_id_pk
--              primary key,
--   n_children integer default 1 not null
--              constraint ccc_n_children_ck
--              check (n_children>=1)
--);

-- XXX organization index;
