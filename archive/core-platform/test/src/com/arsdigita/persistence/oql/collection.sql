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
-- $Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/collection.sql#8 $
-- $DateTime: 2003/08/15 13:46:34 $

create table collection (
    element_id INTEGER not null
        constraint collection_element_id_f_4qmqe
          references icles(icle_id),
    test_id INTEGER not null
        constraint collection_test_id_f_faeki
          references tests(test_id),
    constraint collect_elem_id_tes_id_p_zk_qs
      primary key(test_id, element_id)
)
