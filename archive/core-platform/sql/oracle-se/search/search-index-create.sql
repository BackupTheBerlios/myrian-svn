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

-- search-index-create.sql

-- Create the indexes on the search_content table.


-- Note: the AUTO_SECTION_GROUP option is used to index XML documents.
-- See: http://hojohn.photo.net/ora817/DOC/appdev.817/a86030/adx05t10.htm

BEGIN
   ctx_ddl.create_section_group('autogroup', 'AUTO_SECTION_GROUP');
END;
/

-- Indices created on the content.

CREATE INDEX xml_content_index ON search_content(xml_content) INDEXTYPE IS ctxsys.context
   parameters('filter ctxsys.null_filter section group autogroup');

CREATE INDEX raw_content_index ON search_content(raw_content) INDEXTYPE IS ctxsys.context;

