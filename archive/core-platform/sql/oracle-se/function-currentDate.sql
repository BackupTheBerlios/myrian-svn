-- This is a function to allow the postgres and oracle PDL to 
-- not have to hard code current_timestamp() and sysdate
-- respectively
create or replace function currentDate
return date 
as 
begin 
   return sysdate;
end;
/ 
show errors;
