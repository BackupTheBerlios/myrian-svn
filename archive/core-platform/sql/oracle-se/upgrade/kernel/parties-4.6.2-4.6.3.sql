
-----------------------------------------------------
-- FILE: kernel/sql/oracle-se/parties-create.sql   --
-- CHANGE: Make acs_objects index organized.       --
--                                                 --

-- Note: this upgrade will lose any                
-- "on delete set null" clauses on foreign keys to 
-- acs_objects

alter table acs_objects rename to acs_objects_old;

create table acs_objects (
	object_id		integer not null
				    constraint acs_objects_id_pk primary key,
	object_type		varchar2(100) not null,
    display_name    varchar2(200) not null
) organization index;

insert into acs_objects (object_id, object_type, display_name)
select object_id, object_type, object_type || ' ' || object_id 
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
