--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/adpl.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- Create US counties.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/places/pl-us-counties-create.sql#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $
--

create table pl_us_counties (
    county_id                   constraint pl_usc_county_id_fk
                                references places (place_id)
                                constraint pl_us_counties_pk
                                primary key,
    fips_code                   char(6)
                                constraint pl_usc_fips_code_nn
                                not null,
    state_fips_code             constraint pl_usc_state_fips_code_fk
                                references pl_us_states (fips_code)
                                constraint pl_usc_state_fips_code_nn
                                not null
);

create unique index pl_usc_fips_state_fips_idx
    on pl_us_counties (fips_code, state_fips_code);
