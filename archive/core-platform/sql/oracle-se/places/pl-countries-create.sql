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
-- Create countries.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/places/pl-countries-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $
--

create table pl_countries (
    country_id                  constraint pl_countries_country_id_fk
                                references places (place_id)
                                constraint pl_countries_pk
                                primary key,
    iso                         char(2)
                                constraint pl_countries_iso_un
                                unique
                                constraint pl_countries_iso_nn
                                not null
);
