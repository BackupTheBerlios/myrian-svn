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

--
-- This file contains the data model for the package dependency
-- test cases.
--
-- @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
-- @version $Revision: #2 $ $Date: 2002/07/18 $
--

create table t_package (
    package_id    integer not null constraint package_pk primary key,
    name       varchar(100) not null constraint package_name_un unique
);

create table t_class (
    class_id    integer not null constraint class_pk primary key,
    package_id   not null constraint package_id_fk references t_package(package_id),
    name       varchar(100) not null,
    is_abstract number(1)
);

-- Describes which packages a given package depends on.
-- Also known as Efferent packages.
create table t_package_depends_on (
    package_id                    not null  
                                  constraint t_pack_depend_pack_id_fk
                                  references t_package(package_id),
    depends_on_package_id         not null 
                                  constraint t_pack_depend_de_pack_id_fk
                                  references t_package(package_id)
);


-- Describes which packages use a given package.
-- Also known as Afferent packages.
create table t_package_used_by (
    package_id                 not null  
                               constraint t_pack_used_by_pack_id_fk
                               references t_package(package_id),
    used_by_package_id         not null 
                               constraint t_pack_used_by_used_pack_id_fk
                               references t_package(package_id)
);

create or replace function package_abstractness(v_id integer) return number
is
abs_count number;
total number;
begin
	select count(*) into abs_count from t_class  where package_id = v_id and is_abstract = 1;
	select count(*) into total from t_class  where package_id = v_id;
	return abs_count / total;
end;
/

