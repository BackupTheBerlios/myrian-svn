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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/preferences/preferences-4.6.4-4.6.5.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $

		
create table preferences  (
    preference_id 		integer
			        constraint preferences_pk primary key
				constraint preferences_fk references acs_objects(object_id) 
				on delete cascade,
    parent_id			integer
				constraint preferences_parent_fk 	
				references preferences(preference_id) 
			        on delete cascade,
    name			varchar2(80)
				constraint preferences_name_nn not null,
    description			varchar2(4000),
    is_node                     number(1) default 0
				constraint preferences_is_node_ck
                                    check (is_node in (0,1)),
    preference_type		varchar2(16)
				constraint preferences_type_ck
				check (preference_type in ('user', 'system')),
    --
    -- here go the parameter values in case this is a parameter
    --
    value_type                  varchar2(20)
                                    constraint preference_values_type_ck 
                                    check (value_type in ('int', 'long',
						'float','double', 
                                                'boolean', 'string', 'bytearray')),
    --
    -- the value string can hold int, long, float, double, boolean, string and byte[] 
    value_string		varchar2(4000)
);


-- tripe unique index b.c we have user + system root nodes, both with same name ("") and parent_id = null
create unique index preferences_parent_name_uidx on preferences(parent_id, name, preference_type);

comment on table preferences is 'The table stores the preference information.  
An entry can either be a preference node or a key-value pair parameter, this is 
stored in is_node column';

comment on column preferences.parent_id is 'The parent preference';

comment on column preferences.name is 'Must be unique within the same level of the hierarchy';

comment on column preferences.is_node is 'Whether the preference is a node or key-value 
pair. Note that in case of a node, we have no entries in the value_* fields';

comment on column preferences.preference_type is 'Currently we see ''user'' and ''system'' 
preference types; this might be extended in the future';

comment on column preferences.value_type is
'null is a legitimate value, eg., in case someone specifies an empty string';