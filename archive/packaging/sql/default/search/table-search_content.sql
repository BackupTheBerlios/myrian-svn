--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/default/search/table-search_content.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

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
