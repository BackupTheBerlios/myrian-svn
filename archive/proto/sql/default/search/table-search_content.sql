create table search_content (
    object_id         	integer
			constraint search_content_id_fk references
			acs_objects (object_id) on delete cascade
	          	constraint search_content_pk primary key,
    object_type         varchar(100), -- Same as acs_object(object_type)
                        -- denormalized to reduce joins
    link_text           varchar(1000),
    url_stub            varchar(100),
    summary             varchar(4000),
    xml_content         clob,  -- xml content to be indexed
    raw_content         blob,  -- non-xml content to be indexed
    language            varchar(3)
);
