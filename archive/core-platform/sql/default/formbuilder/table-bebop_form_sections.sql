create table bebop_form_sections (
       form_section_id           integer
                                 constraint bebop_form_sections_id_fk
                                 references bebop_components (component_id)
                                 constraint bebop_form_sections_pk
                                 primary key,
       action                    varchar(500)
);
