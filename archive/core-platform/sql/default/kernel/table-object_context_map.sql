-- need another copy of object-context mappings in order to avoid mutation 
-- errors in the triggers
create table object_context_map (
       object_id            integer not null
                            constraint ocm_object_id_fk 
                            references acs_objects (object_id)
                            constraint ocm_object_id_pk primary key,
       context_id           constraint ocm_context_id_fk
                            references acs_objects(object_id)
) organization index;
