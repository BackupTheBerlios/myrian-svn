--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/5.2.1-6.0.0/vcx_deferred.sql#2 $
-- $DateTime: 2003/08/15 13:46:34 $

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
