create table acs_stylesheets ( 
	stylesheet_id	integer constraint acs_stylesheets_pk primary key
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
