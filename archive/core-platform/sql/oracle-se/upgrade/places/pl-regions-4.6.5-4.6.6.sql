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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/places/pl-regions-4.6.5-4.6.6.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


--
-- Create regions.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/upgrade/places/pl-regions-4.6.5-4.6.6.sql#3 $ by $Author: dennis $, $DateTime: 2002/10/16 14:12:35 $
--

create table pl_regions (
    region_id                   constraint pl_regions_region_id_fk
                                references places (place_id)
                                constraint pl_regions_pk
                                primary key
);
