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
