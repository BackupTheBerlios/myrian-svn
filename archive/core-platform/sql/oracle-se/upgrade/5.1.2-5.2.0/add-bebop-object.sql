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
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/5.1.2-5.2.0/add-bebop-object.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

create table bebop_object_type (
    type_id integer
        constraint bebop_object_type_type_id_fk references
        acs_objects (object_id) on delete cascade
        constraint bebop_object_type_type_id_pk primary key,
    app_type varchar(20)
        constraint bebop_object_type_app_nn not null,
    class_name varchar(120)
        constraint bebop_object_type_class_nn not null,
    constraint bebop_object_type_un unique(app_type, class_name)
);

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

create table bebop_meta_object (
    object_id integer
        constraint bebop_meta_obj_object_id_fk references
        acs_objects on delete cascade
        constraint bebop_meta_obj_object_id_pk primary key,
    type_id integer
        constraint bebop_meta_object_type_id_nn not null
        constraint bebop_meta_object_type_id_fk references
        bebop_object_type on delete cascade,
    pretty_name varchar(50),
    pretty_plural varchar(50),
    class_name varchar(200),
    props_form varchar(200),
    constraint bebop_meta_obj_un unique (type_id, class_name)
);

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

