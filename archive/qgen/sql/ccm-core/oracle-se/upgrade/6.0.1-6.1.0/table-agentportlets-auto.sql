create table agentportlets (
    portlet_id INTEGER not null
        constraint agentportlet_portle_id_p_0q9hq
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    superportlet_id INTEGER not null
        -- referential constraint for superportlet_id deferred due to circular dependencies
);
