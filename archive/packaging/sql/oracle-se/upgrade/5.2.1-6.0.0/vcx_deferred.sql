alter table vcx_blob_operations add 
    constraint vcx_blob_operations_id_f_k6lvg foreign key (id)
      references vcx_operations(id);
alter table vcx_clob_operations add 
    constraint vcx_clob_operations_id_f_a0bts foreign key (id)
      references vcx_operations(id);
alter table vcx_generic_operations add 
    constraint vcx_gener_operation_id_f_ew93q foreign key (id)
      references vcx_operations(id);
alter table vcx_obj_changes add 
    constraint vcx_obj_changes_txn_id_f_e9wcq foreign key (txn_id)
      references vcx_txns(id);
alter table vcx_operations add 
    constraint vcx_operati_eve_typ_id_f_fiy80 foreign key (event_type_id)
      references vcx_event_types(id);
alter table vcx_operations add 
    constraint vcx_operation_chang_id_f_xkahi foreign key (change_id)
      references vcx_obj_changes(id);
alter table vcx_operations add 
    constraint vcx_operation_class_id_f_mqd9i foreign key (class_id)
      references vcx_java_classes(id);
alter table vcx_tags add 
    constraint vcx_tags_txn_id_f_ckn41 foreign key (txn_id)
      references vcx_txns(id);
alter table vcx_txns add 
    constraint vcx_txn_modifying_user_f_c9hs8 foreign key (modifying_user)
      references users(user_id);
