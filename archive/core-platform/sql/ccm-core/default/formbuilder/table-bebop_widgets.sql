--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/table-bebop_widgets.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table bebop_widgets (
    widget_id             integer
                          constraint bebop_widgets_id_fk
                          references bebop_components (component_id)
			              constraint bebop_widgets_pk
			              primary key,
    parameter_name        varchar(100),
    parameter_model       varchar(150),
    default_value         varchar(4000)
);
