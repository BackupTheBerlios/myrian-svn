create table preferences  (
    preference_id               integer
                                constraint preferences_pk primary key,
    parent_id                   integer
                                constraint preferences_parent_fk
                                references preferences (preference_id)
                                on delete cascade,
    name                        varchar(80)
                                constraint preferences_name_nn
                                not null,
    description                 varchar(4000),
    is_node                     number
                                default 0
                                constraint preferences_is_node_ck
                                check (is_node in (0, 1)),
    preference_type             varchar(16)
                                constraint preferences_type_ck
                                check (preference_type in ('user', 'system')),
    value_type                  varchar(20)
                                constraint preference_values_type_ck
                                check (value_type in (
                                                      'int',
                                                      'long',
                                                      'float',
                                                      'double',
                                                      'boolean',
                                                      'string',
                                                      'bytearray'
                                                     )
                                      ),
    value_string                varchar(4000)
);
