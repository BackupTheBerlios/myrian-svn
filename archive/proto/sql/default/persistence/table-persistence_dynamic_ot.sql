create table persistence_dynamic_ot (
    pdl_id                 integer
                           constraint persist_dynamic_ot_pdl_id_fk
                           references acs_objects on delete cascade
                           constraint persist_dynamic_ot_pdl_id_pk
                           primary key,
    pdl_file               clob not null,
    dynamic_object_type    varchar(700) 
                           constraint persist_dynamic_ot_dot_un
                           unique                            
);
