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
-- $Id: //core-platform/test-packaging/sql/default/versioning/table-vc_generic_operations.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table vc_generic_operations (
  operation_id      integer 
    constraint vc_gen_operations_pk primary key
    constraint vc_gen_operations_fk references vc_operations
    on delete cascade,
  datatype          varchar(200),
  old_value         varchar(4000),
  new_value         varchar(4000)
);
