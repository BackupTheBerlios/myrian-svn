create view granted_trans_context_map
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, granted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select object_id, object_id, 0
from object_grants;
