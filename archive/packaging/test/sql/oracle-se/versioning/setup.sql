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
-- $Id: //core-platform/test-packaging/test/sql/oracle-se/versioning/setup.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $


--
-- This file contains the data model for the versioning tests
--
-- @author <a href="mailto:jbank@mit.edu">jbank@arsdigita.com</a>
-- @version $Revision: #2 $ $Date: 2003/08/19 $
--

create table t_versioned_datatypes (
    id               integer
                     constraint t_versioned_fk
                     references acs_objects
                     constraint t_versioned_row_id_pk primary key,
    j_big_integer    integer,
    j_big_decimal    number,
    j_boolean        char(1),
    j_byte           integer,
    j_character      char(1),
    j_date           date,
    j_double         number,
    j_float          number,
    j_integer        integer,
    j_long           integer,
    j_short          integer,
    j_string         varchar(4000),
    j_blob           blob,
    j_clob           clob,
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