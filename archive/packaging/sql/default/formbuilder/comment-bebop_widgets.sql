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
-- $Id: //core-platform/test-packaging/sql/default/formbuilder/comment-bebop_widgets.sql#1 $
-- $DateTime: 2003/08/14 14:53:20 $

comment on table bebop_widgets is '
 Stores data needed specificly to persisting objects of class
 Widget.
';
comment on column bebop_widgets.parameter_name is '
 We currently only support the StringParameter class for the
 parameter model of the widget. This is the name that this
 class takes in its constructor.
';
comment on column bebop_widgets.parameter_name is '
 If a process listener does not dictate a certain parameter model
 it might be desirable for an admin to be able to set one.
';
comment on column bebop_widgets.default_value is '
 This is the default value of the Component. This corresponds to
 the text between the tags or the value attribute in the XHTML representation.
';
