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
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/kernel/objects-4.6.9-4.7.1.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


-- Note: this upgrade will lose any                
-- "on delete set null" clauses on foreign keys to 
-- acs_objects

alter table acs_objects rename to acs_objects_old;

create table acs_objects (
	object_id		        integer not null
				            constraint acs_objects_pk primary key,
	object_type		        varchar2(100) not null,
    display_name            varchar2(200) not null,
    default_domain_class    varchar2(100)
);

insert into acs_objects (object_id, object_type, display_name, default_domain_class)
select object_id, object_type, display_name, default_domain_class
from acs_objects_old;

commit;

declare
  v_st varchar2(4000);
  v_r_cols varchar2(500);
  v_t_cols varchar2(500);
  v_acs_obj_constraint varchar2(30);
begin
    v_r_cols := null;
    v_t_cols := 'OBJECT_ID';

    select constraint_name into v_acs_obj_constraint
    from user_constraints 
    where table_name='ACS_OBJECTS_OLD' 
    and constraint_type = 'P';

    for r in (select constraint_name, table_name, 
              decode(delete_rule, 
                     'CASCADE', 'ON DELETE CASCADE', '') as on_delete_clause
              from user_constraints
              where constraint_type='R'
              and r_constraint_name=v_acs_obj_constraint) loop


        select column_name into v_r_cols
        from user_cons_columns
        where constraint_name = r.constraint_name;
    
        v_st := 'alter table ' || r.table_name ||
                ' drop constraint ' || lower(r.constraint_name);

        -- dbms_output.put_line(v_st);
        execute immediate v_st;

        v_st := 'alter table ' || r.table_name || 
             ' add constraint ' || lower(r.constraint_name) ||
             ' foreign key (' || v_r_cols || ') references' ||
             ' acs_objects (' || v_t_cols || ') ' ||
             r.on_delete_clause;


        -- dbms_output.put_line(v_st);
        execute immediate v_st;

    end loop;
end;
/
show errors;

drop table acs_objects_old;

create table object_container_map (
    object_id               integer not null
                            constraint aocm_object_id_fk
                            references acs_objects (object_id)
                            constraint aocm_object_id_pk primary key,
    container_id            integer not null
                            constraint aocm_container_id_fk
                            references acs_objects (object_id)
) organization index;

create index ocm_container_object_idx on object_container_map (container_id, object_id);

create or replace view object_package_map as
select o.object_id, p.package_id
from acs_objects o, apm_packages p
where p.package_id=o.object_id 
   or p.package_id in (select container_id
                       from object_container_map
                       start with object_id = o.object_id
                       connect by prior container_id = object_id);

