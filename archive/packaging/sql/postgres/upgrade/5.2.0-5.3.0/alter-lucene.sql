--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-packaging/sql/postgres/upgrade/5.2.0-5.3.0/alter-lucene.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $


-- XXX PG 7.2 doesn't let us drop columns easily

create table temp as select * from lucene_docs;
drop table lucene_docs;

create table lucene_docs (
    document_id INTEGER not null
        constraint lucen_docs_document_id_p_2riv8
          primary key,
    content TEXT,
    country CHAR(2),
    creation_date TIMESTAMP,
    creation_party INTEGER,
    dirty INTEGER not null,
    is_deleted BOOLEAN not null,
    language CHAR(2),
    last_modified_date TIMESTAMP,
    last_modified_party INTEGER,
    summary VARCHAR(4000),
    timestamp TIMESTAMP not null,
    title VARCHAR(4000) not null,
    type VARCHAR(200) not null,
    type_info VARCHAR(4000)
);

insert into lucene_docs select document_id, content, country,
  creation_date, creation_party, 2147483647, is_deleted,
  language, last_modified_date, last_modified_party,
  summary, timestamp, title, type, type_info from temp;

drop table temp;
