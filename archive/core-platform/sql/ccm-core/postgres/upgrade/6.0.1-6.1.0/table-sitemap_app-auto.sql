create table sitemap_app (
    application_id INTEGER not null
        constraint sitem_app_applicati_id_p_lb0a5
          primary key
        -- referential constraint for application_id deferred due to circular dependencies
);
