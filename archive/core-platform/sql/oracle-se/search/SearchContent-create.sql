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

-- SearchContent-create.sql

-- Data model for a searchable content.  This data model consists of table
-- search_content and intermedia indexes on the table.  Table search_content
-- stores the content that is to be searched.  The content is written into the
-- table when changes are made to objects that implement Searchable and have
-- a SearchableObserver.  See the Searchable.java file for a summary
-- of how to create a searchable object.

-- Author: Jeff Teeters (teeters@arsdigita.com)

-- $Id: //core-platform/dev/sql/oracle-se/search/SearchContent-create.sql#1 $


-- Table that stores content to be indexed.

create table search_content (
    object_id         	integer
			constraint search_content_id_fk references
			acs_objects (object_id) on delete cascade
	          	constraint search_content_pk primary key,
    object_type         varchar2(100), -- Same as acs_object(object_type)
                        -- denormalized to reduce joins
    link_text           varchar2(1000),
    url_stub            varchar2(100),
    summary             varchar2(4000),
    xml_content         clob,  -- xml content to be indexed
    raw_content         blob,  -- non-xml content to be indexed
    language            varchar2(3)
);



