alter table cat_categories add url varchar (200);

drop function last_attr_value(varchar,integer);

drop trigger acs_permissions_cascade_del_tr on acs_objects;
drop function acs_permissions_cascade_del_fn();
