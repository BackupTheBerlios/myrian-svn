create table forms_dd_select (
    widget_id integer 
        constraint forms_dds_widget_id_pk primary key
        constraint forms_dds_widget_id_fk 
        references bebop_widgets (widget_id) on delete cascade,
    multiple_p char(1) check (multiple_p in ('1', '0')),
    query_id integer
        constraint forms_dds_query_id_fk
        references forms_dataquery (query_id) on delete cascade
);
