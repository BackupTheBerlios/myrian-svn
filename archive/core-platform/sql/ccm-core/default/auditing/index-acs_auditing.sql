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
-- $Id: //core-platform/dev/sql/ccm-core/default/auditing/index-acs_auditing.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

-- Create foreign key indexes
create index acs_auditing_creation_user_idx on acs_auditing(creation_user);
create index acs_auditing_modifyin_user_idx on acs_auditing(modifying_user);
