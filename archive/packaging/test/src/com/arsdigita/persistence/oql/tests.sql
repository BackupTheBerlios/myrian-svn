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
-- $Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/oql/tests.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

create table tests (
    test_id INTEGER not null
        constraint tests_test_id_p_cq728
          primary key,
    name VARCHAR(200),
    optional_self_id INTEGER
        constraint tests_optional_self_id_f_5060l
          references tests(test_id),
    optional_id INTEGER
        constraint tests_optional_id_f_n9xio
          references icles(icle_id),
    required_id INTEGER not null
        constraint tests_required_id_f_swp2a
          references icles(icle_id),
    parent_id INTEGER
        constraint tests_parent_id_f_hlfvv
          references tests(test_id)
)
