--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/comment-bebop_object_type.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

comment on table bebop_object_type is '
  This table defines the base object types whose subclasses we
  need to store meta data on.
';
comment on column bebop_object_type.type_id is '
  The unique indentifier for the object base type. This does
  not need to be a sequence since the table is statically
  populated at install time.
';
comment on column bebop_object_type.app_type is '
  The type of application using the object type.
';
comment on column bebop_object_type.class_name is '
  The fully qualified java class name of the base type.
';
