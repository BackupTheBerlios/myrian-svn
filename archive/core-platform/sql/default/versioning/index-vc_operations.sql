create index vc_operations_action_idx on vc_operations(action);
-- index foreign keys
create index vc_operations_transaction_idx on vc_operations(transaction_id);
