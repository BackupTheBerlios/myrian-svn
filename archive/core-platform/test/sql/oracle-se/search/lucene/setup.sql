create table l_notes (
    note_id integer primary key references acs_objects(object_id),
    name varchar(100) not null,
    text CLOB
);
