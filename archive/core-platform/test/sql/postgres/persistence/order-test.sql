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
-- This file sets up all the data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #3 $ $Date: 2002/07/30 $
--

create table t_orders (
    order_id    integer primary key,
    buyer       varchar(100) not null,
    seller      varchar(100),
    shipping_address varchar(200),
    shipping_date timestamp,
    shipped_p char(1)
);

create table t_line_items (
    item_id    integer primary key,
    order_id   integer not null references t_orders(order_id),
    price      numeric not null,
    name       varchar(100) not null,
    in_stock_p char(1)
);

create table t_other_items (
    other_id    integer primary key,
    price      numeric not null,
    name       varchar(100) not null,
    in_stock_p char(1)
);
