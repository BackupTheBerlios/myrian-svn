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
-- $Id: //core-platform/test-packaging/sql/default/formbuilder/comment-bebop_options.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

comment on table bebop_options is '
 As you will notice, bebop_options is similar to bebop_widgets. I contemplated
 modeling options as widgets. However, Bebop didnt choose to do this and in the interest
 of mimicking Bebop as closely and possible I am letting Options be its own data type
 that extends Component. Options are mapped to Widgets via the table
 bebop_component_hierarchy.
';
comment on column bebop_options.parameter_name is '
 This is the HTML name of the option (identical to that of a Widget).
';
comment on column bebop_options.label is '
 This is the label of the option that is displayed to the user.
';
