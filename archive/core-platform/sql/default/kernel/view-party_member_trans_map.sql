create view party_member_trans_map
as select user_id as party_id, user_id as member_id 
   from users
   UNION ALL
   select group_id as party_id, member_id
   from group_member_trans_index;
