--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/proto/test/sql/postgres/versioning/setup.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


--
-- This file contains the data model for the versioning tests
--
-- @author <a href="mailto:jbank@mit.edu">jbank@arsdigita.com</a>
-- @version $Revision: #1 $ $Date: 2002/11/27 $
--

create table t_versioned_datatypes (
    id               integer
                     constraint t_versioned_fk
                     references acs_objects
                     constraint t_versioned_row_id_pk primary key,
    j_big_integer    integer,
    j_big_decimal    decimal,
    j_boolean        char(1),
    j_byte           integer,
    j_character      char(1),
    j_date           date,
    j_double         numeric,
    j_float          float,
    j_integer        integer,
    j_long           numeric,
    j_short          integer,
    j_string         varchar(4000),
    j_blob           bytea,
    j_clob           text,
    -- forward relation
    related_id      integer references t_versioned_datatypes
                    on delete set null,
    -- backward relation
    parent_id       integer references t_versioned_datatypes
                    on delete cascade,
    -- composite child 
    child_id        integer references t_versioned_datatypes
                    on delete cascade
);

create table t_versioned_map (
    id integer references t_versioned_datatypes on delete cascade, 
    child_id integer references t_versioned_datatypes on delete cascade
);
