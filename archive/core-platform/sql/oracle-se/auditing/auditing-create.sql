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

--Data model for Auditing of an ACS Object

--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author Joseph Bank (jbank@arsdigita.com)
--
-- $Id: //core-platform/dev/sql/oracle-se/auditing/auditing-create.sql#1 $

create table acs_auditing (
        object_id          constraint audited_acs_object_id_fk
                           references acs_objects on delete cascade
                           constraint audited_acs_object_id_pk
                           primary key,
	creation_user      constraint audited_creation_user_fk
                           references users on delete set null,
	creation_date      date not null,
	creation_ip        varchar2(50),
	last_modified      date not null,
	modifying_user     constraint audited_modifying_user_fk
                           references users on delete set null,
	modifying_ip       varchar2(50)
);

-- Create foreign key indexes
create index acs_auditing_creation_user_idx on acs_auditing(creation_user);
create index acs_auditing_modifyin_user_idx on acs_auditing(modifying_user);
