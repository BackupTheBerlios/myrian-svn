--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

-- Data model for ACS Categorization.
--

--
-- Copyright (C) 2001 Arsdigita Corporation
-- @author Randy Graebner (randyg@arsdigita.com)
--
-- $Id: //core-platform/dev/sql/oracle-se/categorization/categorization-create.sql#2 $


create table cat_categories (
    category_id        integer
                       constraint cat_categories_fk
                       references acs_objects on delete cascade
                       constraint cat_categories_pk
                       primary key,
    name               varchar2(200) not null,
    description        varchar(4000),
    -- this should deafult to the JDBC version of true
    enabled_p          char(1),
    -- if the category is abstract then that means it cannot have
    -- any child objects (but it can have child categories).
    abstract_p          char(1) default '0'
                        constraint cat_categories_abstract_p_ck
                        check(abstract_p in ('0','1'))
);


comment on table cat_categories is '
  Defines a category.
';

comment on column cat_categories.enabled_p is '
 To let content producers suggest categories that can later be approved
 by admins. This is a form of collaborative categorization.
';


create table cat_category_category_map (
  category_category_map_id integer
                          constraint cat_cat_cat_map_id_nn
                          not null
                          constraint cat_cat_map_id_pk
                          primary key,
  category_id             integer
                          constraint cat_cat_map_parent_id_fk
                          references cat_categories on delete cascade,
  related_category_id     integer
                          constraint cat_cat_map_category_id_fk
                          references cat_categories on delete cascade,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_cat_map_default_p_ck
                          check(default_p in ('0','1')),
  sort_key                integer,
  relation_type           varchar(10) 
                          constraint cat_cat_map_rel_type_ck
                          check(relation_type in ('child','related')),
  constraint cat_cat_cat_map_ckone
  check(not category_id = related_category_id),
  constraint cat_cat_catmap_un
  unique(category_id, related_category_id)
);


comment on table cat_category_category_map is '
  Holds parent-child associations between categories, including the 
  default parent category.
';


create table cat_object_category_map (
  object_category_map_id  integer
                          constraint cat_obj_cat_map_id_pk
                          primary key,
  category_id             integer
                          constraint cat_obj_cat_map_cat_id_fk
                          references cat_categories on delete cascade,
  object_id               integer
                          constraint cat_obj_map_object_id_fk
                          references acs_objects on delete cascade,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_obj_map_default_p_ck
                          check(default_p in ('0','1')),
  sort_key                integer,
  constraint cat_obj_cat_map_ckone
  check(not category_id = object_id),
  constraint cat_obj_cat_map_un
  unique(category_id, object_id)
);

-- foreign key index. Also needed to significantly reduce io when
-- looking for categories for an object
create index cat_obj_cat_map_object_id_idx on cat_object_category_map(object_id);


comment on table cat_object_category_map is '
  Holds associations between objects and categories, including the 
  primary category.
';



create table cat_object_root_category_map (
       root_category_id           integer
                                 constraint cat_obj_root_map_fk
                                 references cat_categories on delete cascade,
       package_id                integer
                                 constraint cat_obj_package_id_fk
                                 references apm_packages 
                                 on delete cascade,
       -- most of the time the object_id is actually going to
       -- be a user_id and used for personalizing categories
       -- hierarchies within a package
       object_id                 integer
                                 constraint cat_obj_object_id_fk
                                 references acs_objects on delete cascade,
       -- this is used to allow for package type mappings
       -- e.g. if every bboard wants to have the same category
       --  then leave package_id and object_id null and set
       --  object_type to 'bboard'
       object_type               varchar(100)
);

-- foreign key indexes
create index cat_object_root_cat_object_idx on cat_object_root_category_map(object_id);
create index cat_object_root_cat_pkg_idx on cat_object_root_category_map(package_id);
create index cat_object_root_cat_rt_cat_idx on cat_object_root_category_map(root_category_id);

-- Category purposes are used to define what the various top-level
-- branches (i.e. direct children of the "/" category) are to be used for
create table cat_purposes (
    purpose_id         integer
                       constraint cat_purposes_purpose_id_fk
                       references acs_objects on delete cascade
                       constraint cat_purposes_pk
                       primary key,
    key                varchar(40) not null
                       constraint cat_purposes_key_un unique,
    name               varchar(200) not null,
    description        varchar(4000)
);


comment on table cat_purposes is '
   Used to define what the various top-level branches 
   (i.e. direct children of the "/" category) are to be used for
';

comment on column cat_purposes.key is '
   Unique integer defined via static final ints in the Java domain class
';
comment on column cat_purposes.name is '
   Name is used to display the category purpose in select lists, etc.
';


create table cat_category_purpose_map (
  category_id             integer
                          constraint cat_cat_pur_map_cat_id_fk
                          references cat_categories on delete cascade,
  purpose_id              integer
                          constraint cat_obj_map_purpose_id_fk
                          references acs_objects on delete cascade,
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id)
);

create index cat_cat_pur_map_cat_id_idx on cat_category_purpose_map(category_id);
