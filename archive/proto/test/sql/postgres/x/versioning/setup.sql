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
-- $Id: //core-platform/proto/test/sql/postgres/x/versioning/setup.sql#9 $
-- 

--
-- Versioning tests.
--
-- Author: Vadim Nasardinov (vadimn@redhat.com)
-- Since:  2003-02-24
-- Version: $Id: //core-platform/proto/test/sql/postgres/x/versioning/setup.sql#9 $
--          $DateTime: 2003/05/08 17:08:03 $

create table te_vt1 (
    id               integer
                     constraint te_vt1_pk primary key,
    name             varchar(100),
    content          text,
    int_attr         integer
);

create table te_vt2 (
    id               integer
                     constraint te_vt2_pk primary key,
    name             varchar(100),
    unver_attr       varchar(100)
                     constraint te_vt2_unver_attr_nn not null
);

create table te_c1 (
    id              integer
                    constraint te_c1_pk primary key,
    name            varchar(100),
    composite_id    integer
                    constraint te_c1_comp_id_fk references te_vt2
);

create table te_rt1 (
    id              integer
                    constraint te_rt1_pk primary key,
    name            varchar(100)
                    constraint te_rt1_name_nn not null,
    int_attr        integer
);

create table te_vt3 (
    id               integer
                     constraint te_vt3_pk primary key,
    name             varchar(100),
    rt1_id           integer
                     constraint te_vt3_rt1_id_fk references te_rt1
);

create table te_uvct1 (
    id               integer
                     constraint te_uvct1_pk primary key,
    name             varchar(100),
    composite_id     integer
                     constraint te_uvct1_comp_id_fk references te_vt2
);
