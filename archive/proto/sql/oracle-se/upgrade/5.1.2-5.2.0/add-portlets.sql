create table portlets (
    portlet_id INTEGER not null
        constraint portlets_portlet_id_p_IvHRF
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    portal_id INTEGER
        -- referential constraint for portal_id deferred due to circular dependencies
);
