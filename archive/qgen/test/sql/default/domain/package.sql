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
-- $Id: //core-platform/test-qgen/test/sql/default/domain/package.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


--
-- This file contains the data model for the package dependency
-- test cases.
--
-- @author Jon Orris
-- @version $Revision: #1 $ $Date: 2003/12/10 $
--

create table t_package (
    package_id    integer not null constraint package_pk primary key,
    name       varchar(100) not null constraint package_name_un unique
);

create table t_class (
    class_id    integer not null constraint class_pk primary key,
    package_id  integer not null constraint package_id_fk 
                                 references t_package(package_id),
    name       varchar(100) not null,
    is_abstract integer
);

-- Describes which packages a given package depends on.
-- Also known as Efferent packages.
create table t_package_depends_on (
    package_id                    integer not null  
                                  constraint t_pack_depend_pack_id_fk
                                  references t_package(package_id),
    depends_on_package_id         integer not null 
                                  constraint t_pack_depend_de_pack_id_fk
                                  references t_package(package_id)
);


-- Describes which packages use a given package.
-- Also known as Afferent packages.
create table t_package_used_by (
    package_id                 integer not null  
                               constraint t_pack_used_by_pack_id_fk
                               references t_package(package_id),
    used_by_package_id         integer not null 
                               constraint t_pack_used_by_used_pack_id_fk
                               references t_package(package_id)
);
