--
-- Upgrade from version 4.6.5 - 4.6.6
-- 
-- Changes:
--  * Adds indexes
--  * Adds in missing constraints

--------------------------------------------------
-- table: vc_transactions
--------------------------------------------------
create index vc_transactions_object_id_idx on vc_transactions(object_id);
alter table vc_transactions modify timestamp not null;

--------------------------------------------------
-- table: vc_operations
--------------------------------------------------
create index vc_operations_transaction_idx on vc_operations(transaction_id);
create index vc_operations_action_idx on vc_operations(action);
