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
-- Create US Addresses
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/addresses/us-addresses-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $
--

create table us_addresses (
    address_id                  constraint us_addresses_address_id_fk
                                references acs_objects (object_id)
                                constraint us_addresses_pk
                                primary key,
    line1                       varchar2(200)
                                constraint us_addresses_line1_nn
                                not null,
    line2                       varchar2(200),
    line3                       varchar2(200),
    line4                       varchar2(200),
    city                        varchar2(200)
                                constraint us_addresses_city_nn
                                not null,
    state                       char(2)
                                constraint us_addresses_state_nn
                                not null,
    zip                         char(5)
                                constraint us_addresses_zip_nn
                                not null,
    zip_ext                     char(4)
--    city_id                     constraint us_addresses_city_id_fk
--                                references us_cities (city_id)
--                                constraint us_addresses_city_id_nn
--                                not null,
--    state_id                    constraint us_addresses_state_id_fk
--                                references us_states (state_id)
--                                constraint us_addresses_state_id_nn
--                                not null,
--    zip_id                      constraint us_addresses_zip_id_fk
--                                references us_zip_codes (zip_id)
--                                constraint us_addreses_zip_id_nn
--                                not null
);
