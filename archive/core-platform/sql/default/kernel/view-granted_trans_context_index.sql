-----------------------
-- DEPRECTATED VIEWS --
-----------------------

-- These views are here to prevent old code from breaking.  These
-- views do not necessarily perform acceptably.

-- Create two special users for representing two logical groups.
-- The Public is a user that represents anyone in the system or anyone
-- that is not authenticated as a registered user.
-- Registerd Users is a user that represents all users registered in the system.

create or replace view granted_trans_context_index
as select o.object_id, map.implied_context_id, n_generations+1 as n_generations
from object_context_map o, granted_context_non_leaf_map map
where o.context_id = map.object_id
UNION ALL
select object_id, object_id, 0
from object_grants;
