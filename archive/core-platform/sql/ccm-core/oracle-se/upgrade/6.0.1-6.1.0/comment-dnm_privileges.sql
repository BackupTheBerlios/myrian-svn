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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privileges.sql#1 $
-- $DateTime: 2004/01/21 12:36:48 $
-- autor: Aram Kananov <aram@kananov.com>

comment on table dnm_privileges is 'The dnm_privileges table is "bitmapped" 
denormalization of acs_privileges and acs_privilege_hierarchy tables. 
For every privilege there is a one row. The privilege is mapped to 
horisontaliy denormalized list of implied privileges. If some privilege is child 
privilege, value of coresponding pd_priv_xxx column set to 1, otherwise to null.
See also table dnm_privilege_col_map.
';
