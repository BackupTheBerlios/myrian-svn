--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- /enterprise/kernel/dev/kernel/sql/new-site-nodes-create.sql
--
-- Creates schema for mapping URLs to Objects through Site Nodes.
--
-- @author Bryan Quinn (bquinn@arsdigita.com) 
-- @creation-date May 16, 2001 20:16:43
-- @version $Id: //core-platform/dev/sql/oracle-se/kernel/site-nodes-create.sql#1 $
--

create table site_nodes (
	node_id		integer
	    	    	constraint site_nodes_node_id_pk
			primary key,
	parent_id       constraint site_nodes_parent_id_fk
			references site_nodes (node_id),
        name		varchar2(100)
			constraint site_nodes_name_ck
			check (name not like '%/%'),
	constraint site_nodes_un
	unique (parent_id, name),
        --denormalized url, it is definitely worth it to store
        --this here instead of traversing the tree every time!
        url             varchar2(4000),
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

create index site_nodes_object_id_idx on site_nodes (object_id);
