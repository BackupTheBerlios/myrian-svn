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
-- $Id: //core-platform/test-packaging/sql/ccm-core/default/formbuilder/comment-bebop_listeners.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $

comment on table bebop_listeners is '
 For storing listener classes that are added to form sections or widgets.
 The table is used for listener types that can have more than one instenance
 mapped to a component. An exception is the PrintListener since a Widget
 can have only one Printlistener.
';
comment on column bebop_listeners.class_name is '
 The class name of the listener. Lets you persist any listener. Precondition is
 that the listener has a default constructor. No attributes will be set.
';
comment on column bebop_listeners.attribute_string is '
 For persistent listeners that need store attributes. Is on XML attribute format
 just like the column in bebop_components.
';
