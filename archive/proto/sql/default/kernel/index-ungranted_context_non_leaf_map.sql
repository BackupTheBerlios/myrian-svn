

-------------
-- INDEXES --
-------------

---- For some reason, this index results in bad oracle errors for
---- the triggers that add/remove contexts.
---- It doesn't seem to impact performance enough to try to make
---- the triggers work with this index, so for now, we'll just
---- leave out the index.
--
-- create unique index ucnlm_context_obj_idx
--      on ungranted_context_non_leaf_map (implied_context_id, object_id);
