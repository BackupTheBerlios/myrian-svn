-- triple unique index since we have user and system root nodes, both with the
-- same name ("") and parent_id = null
create unique index preferences_parent_name_uidx on
    preferences(parent_id, name, preference_type);
