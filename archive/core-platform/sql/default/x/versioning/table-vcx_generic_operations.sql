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
-- $Id: //core-platform/dev/sql/default/x/versioning/table-vcx_generic_operations.sql#1 $
-- $DateTime: 2003/02/07 18:31:46 $

create table vcx_generic_operations (
  operation_id      integer 
    constraint vcx_gen_operations_pk primary key
    constraint vcx_gen_operations_fk references vcx_operations
    on delete cascade,
  datatype          varchar(200),
  old_value         varchar(4000),
  new_value         varchar(4000)
);