create table cat_object_root_category_map (
       root_category_id           integer
                                 constraint cat_obj_root_map_fk
                                 references cat_categories on delete cascade,
       package_id                integer
                                 constraint cat_obj_package_id_fk
                                 references apm_packages 
                                 on delete cascade,
       -- most of the time the object_id is actually going to
       -- be a user_id and used for personalizing categories
       -- hierarchies within a package
       object_id                 integer
                                 constraint cat_obj_object_id_fk
                                 references acs_objects on delete cascade,
       -- this is used to allow for package type mappings
       -- e.g. if every bboard wants to have the same category
       --  then leave package_id and object_id null and set
       --  object_type to 'bboard'
       object_type               varchar(100)
);
