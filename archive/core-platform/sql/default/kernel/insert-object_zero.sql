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
-- $Id: //core-platform/dev/sql/default/kernel/insert-object_zero.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $


-- developers should never access this object directly.
-- The only way to access this object is by checking/granting/revoking
-- UniversalPermissonDescriptors instead of regular PermissionDescriptors.
-- In the future, it is likely that this object will go away or not be
-- an ACSObject.
insert into acs_objects 
(object_id, object_type, display_name, 
 default_domain_class)
values 
(0, 'com.arsdigita.kernel.ACSObject', 'Universal Permission Context', 
 'com.arsdigita.kernel.ACSObject');
