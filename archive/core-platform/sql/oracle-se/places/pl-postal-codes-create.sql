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
-- Create postal codes.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/places/pl-postal-codes-create.sql#3 $ by $Author: dan $, $DateTime: 2002/07/31 10:49:40 $
--

create table pl_postal_codes (
    postal_code_id              constraint pl_pc_postal_code_id_fk
                                references places (place_id)
                                constraint pl_postal_codes_pk
                                primary key,
    postal_code                 varchar(100)
                                constraint pl_pc_postal_code_nn
                                not null
);
