-- denormalize the above hierarchy for speedier access. so if the hierarchy
-- contains the following rows:
--
-- parent       child
--   A            B
--   A            C
--   C            D
--
-- then this denormalization will contain the following rows:
--
-- parent       child
--   A            B
--   A            C
--   C            D
--   A            D
--
-- for those who care, this is called "transitive closure."
--
create table place_hierarchy_tc (
    child_id                    integer constraint place_hier_tc_child_id_fk
                                references places (place_id)
                                constraint place_hierarchy_index_pk
                                primary key,
    parent_id                   integer constraint place_hier_tc_parent_id_fk
                                references places (place_id),
    depth                       integer default 0
                                constraint place_hier_tc_depth_nn
                                not null
);
-- XXX organization index;
