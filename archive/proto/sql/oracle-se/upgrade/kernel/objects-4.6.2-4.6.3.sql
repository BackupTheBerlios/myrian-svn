--
-- Make group name non-nullable.
--
update groups 
set name = (select display_name from acs_objects 
            where object_id=group_id)
where name is null;

alter table groups modify name not null;
