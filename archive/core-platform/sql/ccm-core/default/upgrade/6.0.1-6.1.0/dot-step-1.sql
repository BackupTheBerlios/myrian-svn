--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/6.0.1-6.1.0/dot-step-1.sql#5 $
-- $DateTime: 2004/08/16 18:10:38 $
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
