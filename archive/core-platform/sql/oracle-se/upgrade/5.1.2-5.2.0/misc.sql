alter table acs_permissions modify ( 
    creation_date default null
);

alter table acs_stylesheet_node_map modify (
    stylesheet_id not null,
    node_id not null
);

alter table acs_stylesheets modify (
    output_type default null
);

alter table acs_stylesheet_type_map modify (
    stylesheet_id not null,
    package_type_id not null
);

alter table apm_package_types modify (
    dispatcher_class default null
);

alter table email_addresses modify (
    bouncing_p not null,
    verified_p not null
);

alter table party_email_map modify (
    email_address not null
);

alter table preferences modify (
    is_node number
);

alter table roles modify (
    group_id null
);

alter table site_nodes modify (
    directory_p default null,
    pattern_p default null
);

alter table vc_transactions modify (
    timestamp timestamp default current_timestamp
);

alter table group_member_map drop column id;
alter table group_subgroup_map drop column id;
