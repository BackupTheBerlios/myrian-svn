--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-packaging/sql/default/formbuilder/table-bebop_components.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table bebop_components (
    component_id	    integer
                        constraint bebop_components_id_fk
                        references acs_objects (object_id)
			            constraint bebop_components_pk
			            primary key,
    admin_name          varchar(100),
    description         varchar(4000),
    attribute_string	varchar(4000),
    active_p            char(1)
);
