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
-- $Id: //core-platform/proto/sql/default/categorization/table-cat_purposes.sql#4 $
-- $DateTime: 2003/08/04 15:56:00 $

create table cat_purposes (
    purpose_id         integer
                       constraint cat_purposes_purpose_id_fk
                       references acs_objects
                       constraint cat_purposes_pk
                       primary key,
    key                varchar(40) not null
                       constraint cat_purposes_key_un unique,
    name               varchar(200) not null,
    description        varchar(4000)
);
