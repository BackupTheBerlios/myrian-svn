create table admin_app (
    application_id INTEGER not null
        constraint admi_app_applicatio_id_p_lonsa
          primary key
        -- referential constraint for application_id deferred due to circular dependencies
);
