comment on table roles is '
        This table is used to store metadata about the roles in the
        system. Each role is represented by the Role object type.
';
comment on column roles.group_id is '
        This column refers to the group for which the role was
        created.
';
comment on column roles.implicit_group_id is '
        Temporary hack. Implementation currently creates a subgroup
        for each row. The created subgroup is references by
        implicit_group_id.  
';
