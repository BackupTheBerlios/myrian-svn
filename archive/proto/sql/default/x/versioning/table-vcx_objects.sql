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
-- $Id: //core-platform/proto/sql/default/x/versioning/table-vcx_objects.sql#1 $
-- $DateTime: 2003/04/09 16:35:55 $

create table vcx_objects (
  object_id       integer
    constraint vcx_objects_obj_fk references acs_objects
    on delete cascade,
  is_deleted      char(1) default '0' not null
                  check (is_deleted in ('1', '0')),
  master_id       integer
    constraint vcx_objects_mst_fk references acs_objects
    on delete set null,
  constraint vcx_objects_pk
    primary key(object_id)
);
