--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/add-lucene-docs.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table lucene_docs (
    document_id INTEGER not null
        constraint lucen_docs_document_id_p_2riv8
          primary key,
    content CLOB,
    country CHAR(2),
    creation_date DATE,
    creation_party INTEGER,
    is_deleted CHAR(1) not null,
    is_dirty CHAR(1) not null,
    language CHAR(2),
    last_modified_date DATE,
    last_modified_party INTEGER,
    summary VARCHAR(4000),
    timestamp DATE not null,
    title VARCHAR(4000) not null,
    type VARCHAR(200) not null,
    type_info VARCHAR(4000)
);
