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
-- $Id: //core-platform/dev/sql/oracle-se/versioning/table-vcx_transactions.sql#1 $
-- $DateTime: 2003/02/07 18:31:46 $

create table vcx_transactions (
  transaction_id   integer 
    constraint vcx_transactions_pk primary key,
  master_id        integer
    constraint vcx_trans_masters_fk references vcx_objects
    on delete cascade,
  object_id        integer
    constraint vcx_trans_objects_fk references vcx_objects
    on delete cascade,
  modifying_user   integer,
  modifying_ip     varchar(400),
  timestamp        date default sysdate not null,
  description      varchar(4000),
  tag              varchar(400)
);
