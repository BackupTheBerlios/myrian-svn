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

--
-- This file contains the data model for the party test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2002/07/18 $
--

create table t_parties (
    party_id    integer primary key,
    email varchar(100) not null
);

create table t_colors (
    color_id   integer primary key,
    name       varchar(100) not null
);

create table t_users (
    user_id    integer primary key references t_parties,
    first_name varchar(100) not null,
    last_names varchar(100) not null,
    bio        varchar(4000),
    favorate_color_id integer references t_colors
);

create table t_groups (
    group_id    integer primary key references t_parties,
    name        varchar(100) not null
);

create table t_user_group_map (
    group_id    integer references t_groups,
    member_id   integer references t_users,
    membership_date date default sysdate not null,
    primary key (group_id, member_id)
);
