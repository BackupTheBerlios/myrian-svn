--
-- Views on the above denormalizations
--
create or replace view all_context_non_leaf_map
as select object_id, implied_context_id, n_generations
   from granted_context_non_leaf_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_context_non_leaf_map;
