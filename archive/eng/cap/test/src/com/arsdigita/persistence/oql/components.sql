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

create table components (
    component_id INTEGER not null
        constraint component_component_id_p_6gckk
          primary key,
    name VARCHAR(200),
    test_id INTEGER not null
        constraint components_test_id_f_9042c
          references tests(test_id)
)
