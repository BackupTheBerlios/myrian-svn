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
-- $Id: //core-platform/dev/sql/ccm-core/default/versioning/table-vc_objects.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table vc_objects (
  object_id       integer
    constraint vc_objects_obj_fk references acs_objects
    on delete cascade,
  is_deleted      char(1) default '0' not null
                  check (is_deleted in ('1', '0')),
  master_id       integer
    constraint vc_objects_mst_fk references acs_objects
    on delete set null,
  constraint vc_objects_pk
    primary key(object_id)
);
