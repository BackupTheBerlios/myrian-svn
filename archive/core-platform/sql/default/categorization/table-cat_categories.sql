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
-- $Id: //core-platform/dev/sql/default/categorization/table-cat_categories.sql#3 $
-- $DateTime: 2003/01/07 14:51:38 $

create table cat_categories (
    category_id        integer
                       constraint cat_categories_fk
                       references acs_objects on delete cascade
                       constraint cat_categories_pk
                       primary key,
    name               varchar(200) not null,
    description        varchar(4000),
    -- this should deafult to the JDBC version of true
    enabled_p          char(1),
    -- if the category is abstract then that means it cannot have
    -- any child objects (but it can have child categories).
    abstract_p          char(1) default '0'
                        constraint cat_categories_abstract_p_ck
                        check(abstract_p in ('0','1')),
    default_ancestors varchar(4000) not null
);
