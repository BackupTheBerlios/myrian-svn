--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/aggressive-test.sql#1 $
-- $DateTime: 2004/05/05 16:10:50 $


--
-- This file contains the data model for the party test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2004/05/05 $
--

create table agg_colors (
    color_id   integer primary key,
    name       varchar(100) not null
);

create table agg_users (
    user_id    integer primary key,
    name varchar(100) not null,
    favorite_color_id integer references agg_colors,
    referer_id integer references agg_users
);
