-- index the foreign key
create index vc_transactions_master_id_idx on vc_transactions(master_id);
-- index the timestamp -- avoids full table scans for last_attr_value.
create index vc_transactions_tstamp_idx on vc_transactions(timestamp);
