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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/versioning/table-vc_transactions.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

create table vc_transactions (
  transaction_id   integer 
    constraint vc_transactions_pk primary key,
  master_id        integer
    constraint vc_trans_masters_fk references vc_objects
    on delete cascade,
  object_id        integer
    constraint vc_trans_objects_fk references vc_objects
    on delete cascade,
  modifying_user   integer,
  modifying_ip     varchar(400),
  timestamp        timestamptz default current_timestamp not null,
  description      varchar(4000),
  tag              varchar(400)
);
