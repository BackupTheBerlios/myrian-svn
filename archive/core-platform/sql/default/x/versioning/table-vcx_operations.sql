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
-- $Id: //core-platform/dev/sql/default/x/versioning/table-vcx_operations.sql#2 $
-- $DateTime: 2003/02/12 20:42:45 $



create table vcx_operations (
  operation_id      integer 
    constraint vcx_operations_pk primary key,
  change_id    integer
    constraint vcx_operations_trans_id_fk references vcx_obj_changes
    on delete cascade,
  action            varchar(200)
    constraint vcx_operations_actions_fk references vcx_actions,
  attribute         varchar(200),
  classname         varchar(4000) 
    constraint vcx_operations_classname_nn not null
);
