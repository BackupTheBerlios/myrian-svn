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
-- $Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/collection_self.sql#8 $
-- $DateTime: 2003/08/15 13:46:34 $

create table collection_self (
    element_id INTEGER not null
        constraint collecti_sel_elemen_id_f_rmprl
          references tests(test_id),
    test_id INTEGER not null
        constraint collectio_self_test_id_f_ckdca
          references tests(test_id),
    constraint coll_sel_ele_id_tes_id_p_7m18z
      primary key(test_id, element_id)
)
