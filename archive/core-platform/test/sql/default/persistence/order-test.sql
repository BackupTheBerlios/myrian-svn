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
-- This file contains the data model for the order line item test
-- cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #2 $ $Date: 2002/07/30 $
--



create table t_orders_ext (
    order_id     integer primary key references t_orders,
    text         varchar(100)
);


create table t_order_other_item_map (
      order_id   integer references t_orders,
      other_item_id   integer references t_other_items 
);
