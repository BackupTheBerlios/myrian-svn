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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/parties-4.6.7-4.6.8.sql#3 $
-- $DateTime: 2002/10/16 14:12:35 $


alter table user_authentication modify
	password		varchar2(100) not null;

create index roles_implicit_group_id_idx on roles(implicit_group_id);
