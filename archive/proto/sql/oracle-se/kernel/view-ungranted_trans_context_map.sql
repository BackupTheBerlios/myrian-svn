create view ungranted_trans_context_map
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, ungranted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select o.object_id, o.context_id, 1
from object_context_map o, object_grants g
where o.object_id = g.object_id(+) and g.object_id=null;
