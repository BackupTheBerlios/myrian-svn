--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/5.2.0-5.2.1/alter-requests.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

alter table nt_requests drop constraint nt_requests_message_fk;

alter table nt_requests add constraint nt_requests_message_fk
  foreign key (message_id) references messages (message_id)
  on delete cascade;
