create or replace function currentDate
return date 
as 
begin 
   return sysdate;
end;
/ 
show errors;

