create table bebop_component_hierarchy (
       container_id              integer
                                 constraint bebop_component_hierarchyci_fk
                                 references bebop_components (component_id),
       component_id              integer
                                 constraint bebop_component_hierarchyco_fk
                                 references bebop_components(component_id),
       order_number              integer,
       selected_p                char(1),
       constraint bebop_component_hierarchy_un
       unique(container_id, component_id)
);
