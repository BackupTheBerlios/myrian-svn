-- Use with caution.  For some objects, this view will contain
-- the implicit mapping (object_id, object_id, 0).  For some objects this
-- view WILL NOT contain this implicit mapping.
create view object_context_trans_map
as select object_id, implied_context_id, n_generations
   from granted_trans_context_map
   UNION ALL
   select object_id, implied_context_id, n_generations
   from ungranted_trans_context_map;
