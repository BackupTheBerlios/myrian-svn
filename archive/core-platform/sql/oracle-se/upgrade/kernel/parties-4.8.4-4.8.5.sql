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
-- Data model upgrade from 4.8.4 to 4.8.5
--
-- Copyright (C) 2001 Arsdigita Corporation
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/parties-4.8.4-4.8.5.sql#1 $

alter table party_email_map drop constraint pem_email_address_fk;
alter table parties drop constraint parties_primary_email_fk;
