create table portals (
    portal_id INTEGER not null
        constraint portals_portal_id_p_gaVLM
          primary key,
        -- referential constraint for portal_id deferred due to circular dependencies
    template_p CHAR(1),
    title VARCHAR(200)
);
