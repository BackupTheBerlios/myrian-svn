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
-- $Id: //core-platform/proto/sql/default/kernel/comment-roles.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $

comment on table roles is '
        This table is used to store metadata about the roles in the
        system. Each role is represented by the Role object type.
';
comment on column roles.group_id is '
        This column refers to the group for which the role was
        created.
';
comment on column roles.implicit_group_id is '
        Temporary hack. Implementation currently creates a subgroup
        for each row. The created subgroup is references by
        implicit_group_id.  
';
