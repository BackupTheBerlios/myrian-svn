create table site_nodes (
	node_id		integer
	    	    	constraint site_nodes_node_id_pk
			primary key,
	parent_id       constraint site_nodes_parent_id_fk
			references site_nodes (node_id),
        name		varchar(100)
			constraint site_nodes_name_ck
			check (name not like '%/%'),
	constraint site_nodes_un
	unique (parent_id, name),
        --denormalized url, it is definitely worth it to store
        --this here instead of traversing the tree every time!
        url             varchar(4000),
	-- Is it legal to create a child node?
	directory_p	char(1) default '1' not null 
			constraint site_nodes_directory_p_ck
			check (directory_p in ('0', '1')),
        -- Should urls that are logical children of this node be
	-- mapped to this node?
        pattern_p	char(1) default '0' not null
			constraint site_nodes_pattern_p_ck
			check (pattern_p in ('0', '1')),
	object_id	constraint site_nodes_object_id_fk
			references acs_objects (object_id)
);
