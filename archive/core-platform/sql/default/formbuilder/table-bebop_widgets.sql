create table bebop_widgets (
    widget_id             integer
                          constraint bebop_widgets_id_fk
                          references bebop_components (component_id)
			              constraint bebop_widgets_pk
			              primary key,
    parameter_name        varchar(100),
    parameter_model       varchar(150),
    default_value         varchar(4000)
);
