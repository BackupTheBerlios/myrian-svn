--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/6.0.1-6.1.0/dot-step-1.sql#4 $
-- $DateTime: 2004/04/07 16:07:11 $
alter table persistence_dynamic_ot rename to pdot_backup;
alter table persistence_dynamic_assoc rename to pda_backup;

create table persistence_dynamic_ot (
    pdl_id                 integer,
    pdl_file               varchar(4000),
    dynamic_object_type    varchar(700)
);

create table persistence_dynamic_assoc (
    pdl_id                 integer,
    pdl_file               varchar(4000),
    model_name             varchar(200),
    object_type_one        varchar(500),
    property_one           varchar(100),
    object_type_two        varchar(500),
    property_two           varchar(100)
);
