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
-- $Id: //core-platform/proto/sql/oracle-se/preferences/table-preferences.sql#4 $
-- $DateTime: 2003/08/04 15:56:00 $

create table preferences  (
    preference_id               integer
                                constraint preferences_pk primary key,
    parent_id                   integer
                                constraint preferences_parent_fk
                                references preferences (preference_id),
    name                        varchar(80)
                                constraint preferences_name_nn
                                not null,
    description                 varchar(4000),
    is_node                     number
                                default 0
                                constraint preferences_is_node_ck
                                check (is_node in (0, 1)),
    preference_type             varchar(16)
                                constraint preferences_type_ck
                                check (preference_type in ('user', 'system')),
    value_type                  varchar(20)
                                constraint preference_values_type_ck
                                check (value_type in (
                                                      'int',
                                                      'long',
                                                      'float',
                                                      'double',
                                                      'boolean',
                                                      'string',
                                                      'bytearray'
                                                     )
                                      ),
    value_string                varchar(4000)
);
