--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/update-host-unique-index.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

-- Drop old unique index on server name
alter table web_hosts drop constraint web_hosts_server_name_u_frlsu;
-- Add uniqueness on server name, server port
alter table web_hosts add constraint web_hos_ser_nam_ser_po_u_las14 unique(server_name, server_port);