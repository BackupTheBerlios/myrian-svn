create table acs_stylesheet_type_map (
    stylesheet_id   integer constraint acs_stylesheet_type_sheet_fk
                    references acs_stylesheets
                    on delete cascade,
    package_type_id integer constraint acs_stylesheet_type_type_fk
                    references apm_package_types
                    on delete cascade
);
