-------------
-- INDEXES --
-------------

create unique index gmti_member_group_idx 
    on group_member_trans_index (member_id, group_id);
