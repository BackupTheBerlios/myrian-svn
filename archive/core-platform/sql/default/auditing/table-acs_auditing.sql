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
-- $Id: //core-platform/dev/sql/default/auditing/table-acs_auditing.sql#4 $
-- $DateTime: 2003/05/21 12:25:09 $

create table acs_auditing (
        object_id          integer constraint audited_acs_object_id_fk
                           references acs_objects on delete cascade
                           constraint audited_acs_object_id_pk
                           primary key,
	creation_user      integer constraint audited_creation_user_fk
                           references users,
	creation_date      timestamp not null,
	creation_ip        varchar(50),
	last_modified      timestamp not null,
	modifying_user     integer constraint audited_modifying_user_fk
                           references users,
	modifying_ip       varchar(50)
);
