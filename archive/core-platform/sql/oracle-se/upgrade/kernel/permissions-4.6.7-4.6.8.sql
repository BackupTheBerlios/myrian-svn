--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

-- Upgrades the data model from version 4.6.7 to 4.6.8
--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/kernel/permissions-4.6.7-4.6.8.sql#1 $

create index acs_perm_creation_user_idx on acs_permissions(creation_user);
create index object_context_context_id_idx on object_context(context_id);
