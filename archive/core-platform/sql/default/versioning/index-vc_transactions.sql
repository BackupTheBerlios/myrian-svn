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
-- $Id: //core-platform/dev/sql/default/versioning/index-vc_transactions.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

create index vc_transactions_master_id_idx on vc_transactions(master_id);
-- index the timestamp -- avoids full table scans for last_attr_value.
create index vc_transactions_tstamp_idx on vc_transactions(timestamp);
