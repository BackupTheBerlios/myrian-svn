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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/6.0.1-6.1.0/comment-acs_privilege_hierarchy.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

comment on TABLE ACS_PRIVILEGE_HIERARCHY is '
  To reduce number of permission records, and to simplify permission
queries and associated business logic in Java layer, this table stores
privilege hierarchy.  This hierarchy allows child nodes to have multiple
parents, and parents to have multiple children.
  This table stores only direct (parent,child) mappings, which is often
referred as adjacency model, resulting in small table.  
';

