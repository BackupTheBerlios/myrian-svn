create table place_hierarchy (
    child_id                    constraint place_hierarchy_child_id_fk
                                references places (place_id)
                                constraint place_hierarchy_pk
                                primary key,
    parent_id                   constraint place_hierarchy_parent_id_fk
                                references places (place_id)
);
