--
-- Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
--
-- This program is Open Source software; you can redistribute it and/or
-- modify it under the terms of the Open Software License version 2.1 as
-- published by the Open Source Initiative.
--
-- You should have received a copy of the Open Software License along
-- with this program; if not, you may obtain a copy of the Open Software
-- License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
-- or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
-- 3001 King Ranch Road, Ukiah, CA 95482.

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
