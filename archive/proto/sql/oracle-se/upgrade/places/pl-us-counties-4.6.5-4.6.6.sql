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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/places/pl-us-counties-4.6.5-4.6.6.sql#1 $
-- $DateTime: 2002/10/24 11:07:46 $


--
-- Create US counties.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/proto/sql/oracle-se/upgrade/places/pl-us-counties-4.6.5-4.6.6.sql#1 $ by $Author: dennis $, $DateTime: 2002/10/24 11:07:46 $
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
