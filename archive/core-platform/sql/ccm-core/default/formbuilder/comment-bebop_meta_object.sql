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
-- $Id: //core-platform/dev/sql/ccm-core/default/formbuilder/comment-bebop_meta_object.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

comment on table bebop_meta_object is '
  This table maintains a registry of all meta data for 
  various persistent classes, such as process listeners
  and persistent widgets.
';
comment on column bebop_meta_object.object_id is '
  The unique object identifier for the widget type.
';
comment on column bebop_meta_object.type_id is '
  The id of the base object type.
';
comment on column bebop_meta_object.pretty_name is '
  The user facing ''pretty'' name for the widget type
';
comment on column bebop_meta_object.pretty_plural is '
  The plural equivalent of the pretty_name attribute
';
comment on column bebop_meta_object.class_name is '
  The fully qualified java class name of the widget. The class
  should inherit from com.arsdigita.formbuilder.PersistentWidget
';
comment on column bebop_meta_object.props_form is '
  The fully qualfied java class name of a form section for
  editing the properties of a widget. The class should be a
  subclass of com.arsdigita.bebop.Bebopection.
';
