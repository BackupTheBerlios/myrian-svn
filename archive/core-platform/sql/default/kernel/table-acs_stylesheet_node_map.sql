create table acs_stylesheet_node_map (
    stylesheet_id   integer constraint acs_stylesheet_node_sheet_fk
                    references acs_stylesheets
                    on delete cascade,
    node_id         integer constraint acs_stylesheet_node_node_fk
                    references site_nodes
                    on delete cascade
);
