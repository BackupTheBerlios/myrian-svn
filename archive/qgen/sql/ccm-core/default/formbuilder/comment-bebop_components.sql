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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/formbuilder/comment-bebop_components.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

comment on table bebop_components is '
 Stores data for the Component Data Object (used by
 persistent Bebop Components).
';
comment on column bebop_components.admin_name is '
 A name that helps administrators identify the Component.
';
comment on column bebop_components.description is '
 As description that helps users use the Component.
';
comment on column bebop_components.attribute_string is '
 This is the attribute string of the Component on the XML attribute
 format 
        key1="value1" key2="value2" ... keyN="valueN"
';
comment on column bebop_components.active_p is '
 If this is true the component is active and will be displayed. By
 setting this column to false an admin has disabled a component without
 having to delete it and with the option of activating it later.
';
