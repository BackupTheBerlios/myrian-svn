--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/add-lucene-docs.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
