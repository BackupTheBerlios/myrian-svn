


-----------
-- VIEWS --
-----------

-- This view's implementation will change when we implement denormalizations.
--
--       If there is a path from group A to group B through the subgroup 
--       graph, then this view will contain (A,B).  Note that this implies
--       it would contain (A,A)
--
create view group_subgroup_trans_map
as select group_id, subgroup_id
   from group_subgroup_trans_index;
