create table theme_stylesheet_map (
    theme_id constraint tsm_theme_id_fk references note_themes(theme_id),
    stylesheet_id constraint tsm_stylesheet_id_fk 
                references acs_stylesheets(stylesheet_id),
    primary key (theme_id, stylesheet_id)
);
