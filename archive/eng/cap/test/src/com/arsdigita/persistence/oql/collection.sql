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
