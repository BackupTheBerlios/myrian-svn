create table forms_widget_label (
    label_id integer
        constraint forms_wgt_label_label_id_fk
        references bebop_widgets (widget_id) on delete cascade
        constraint forms_wgt_label_label_id_pk primary key,
    widget_id integer
        constraint forms_wgt_label_widget_id_fk
        references bebop_widgets (widget_id) on delete cascade
);
