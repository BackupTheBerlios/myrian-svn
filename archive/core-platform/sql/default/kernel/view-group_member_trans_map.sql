--       IF user u is a direct member of group B  
--          AND
--          (A,B) is in group_subgroup_trans_map
--       THEN 
--          this view will contain (A,u).  
create view group_member_trans_map
as select group_id, member_id from group_member_trans_index;
