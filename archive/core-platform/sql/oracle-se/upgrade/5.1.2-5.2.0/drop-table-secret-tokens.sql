create or replace procedure ccm_drop_table_if_exist (v_table in VARCHAR)
is 
    table_count integer;
begin
    
    select count(*) into table_count
    from user_tables
    where upper(table_name) = upper(v_table);

    if (table_count = 1) then
      execute immediate 'drop table ' || v_table;
    end if;
end;
/
show errors;

begin
ccm_drop_table_if_exist ('secret_tokens');
end;
/

drop procedure ccm_drop_table_if_exist;