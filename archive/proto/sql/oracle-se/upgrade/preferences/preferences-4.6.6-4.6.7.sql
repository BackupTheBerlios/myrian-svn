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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/preferences/preferences-4.6.6-4.6.7.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $

		
-- this change reflects that preferences moved from kernel to 
-- infrastructure and therefore no longer references acs_objects;

alter table preferences drop constraint preferences_fk;
