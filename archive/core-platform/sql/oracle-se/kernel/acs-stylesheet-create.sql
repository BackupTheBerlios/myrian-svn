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

-- create table for storing XSL stylesheets in content repository
-- 
-- 
-- Bill Schneider (bschneid@arsdigita.com)
-- Bryan Quinn    (bquinn@arsdigita.com)
-- $Id: //core-platform/dev/sql/oracle-se/kernel/acs-stylesheet-create.sql#1 $
-- created 4/11/01
--
-----------

create table acs_stylesheets ( 
	stylesheet_id	constraint acs_stylesheets_pk primary key
                        constraint acs_stylesheet_id_fk 
                        references acs_objects (object_id),
	-- locale/language
    locale_id       integer
                    constraint acs_stylesheets_locale_fk
                    references g11n_locales (locale_id),
	-- output type: HTML, WML, VXML, etc.
	output_type     varchar(50) default 'text/html',
	-- where do we store the XSL markup?
	-- maybe we store it in the file system
	-- store path relative to webapp root, or absolute
	pathname	    varchar(300),
	-- or we could store the XSL  in the content repository
	-- XXX: need to figure out some sane way to install this
	-- data model (circular dependency--need CR installed first!)
	item_id		    integer
	--		        constraint acs_stylesheets_item_id_fk
	--		        references cr_items (item_id),
);

create index acs_stylesheets_locale_id_idx on acs_stylesheets(locale_id);

create table acs_stylesheet_type_map (
    stylesheet_id   constraint acs_stylesheet_type_sheet_fk
                    references acs_stylesheets
                    on delete cascade,
    package_type_id constraint acs_stylesheet_type_type_fk
                    references apm_package_types
                    on delete cascade
);

create index acs_stylesheet_type_sheet_idx on acs_stylesheet_type_map(package_type_id);
create index acs_stylesheet_type_pkg_idx on acs_stylesheet_type_map(stylesheet_id);

create table acs_stylesheet_node_map (
    stylesheet_id   constraint acs_stylesheet_node_sheet_fk
                    references acs_stylesheets
                    on delete cascade,
    node_id         constraint acs_stylesheet_node_node_fk
                    references site_nodes
                    on delete cascade
);

create index acs_stylesheet_node_node_idx on acs_stylesheet_node_map(node_id);
create index acs_stylesheet_node_sheet_idx on acs_stylesheet_node_map(stylesheet_id);
