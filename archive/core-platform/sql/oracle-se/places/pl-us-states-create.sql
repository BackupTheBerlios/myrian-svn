--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/apl.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- Create US states
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/places/pl-us-states-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $
--

create table pl_us_states (
    state_id                    constraint pl_uss_state_id_fk
                                references pl_regions (region_id)
                                constraint pl_uss_states_pk
                                primary key,
    fips_code                   char(2)
                                constraint pl_uss_fips_code_un
                                unique
                                constraint pl_uss_fips_code_nn
                                not null,
    usps_abbrev                 char(2)
                                constraint pl_uss_usps_abbrev_un
                                unique
                                constraint pl_uss_usps_abbrev_nn
                                not null
);
